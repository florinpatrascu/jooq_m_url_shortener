package controllers;

import ca.simplegames.micro.Controller;
import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.extensions.JOOQExtension;
import ca.simplegames.micro.utils.Assert;
import controllers.utils.Base64;
import controllers.utils.Bijective;
import models.tables.Urls;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Calculate the number of hits per MONTH in a given YEAR for a specific URL
 * Using the H2 db syntax and functions, will be something like this:
 * <p/>
 * - select url_id, count(*) as total_hits from hits
 * where url_id = 3 and YEAR(created_at) = 2014 and MONTH(created_at)=4
 * group by YEAR(created_at), MONTH(created_at)
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2014-03-30)
 */
public class HitStatsController implements Controller {
  public void execute(MicroContext context, Map configuration) throws ControllerException {
    @SuppressWarnings(value = "unchecked")
    Map<String, Object> params = (Map<String, Object>) context.get(Globals.PARAMS);
    SiteContext site = context.getSiteContext();
    JOOQExtension jOOQ = (JOOQExtension) site.get("jOOQ");
    Assert.notNull(jOOQ, "Please install the jOOQ extension.");

    String url = (String) params.get("url");
    String year = (String) params.get("year");

    url = url == null || url.isEmpty() ? Globals.EMPTY_STRING : url;
    year = year == null || year.isEmpty() ? Globals.EMPTY_STRING : year;

    JOOQExtension.MDSL dsl = null;

    try {
      dsl = jOOQ.getDSL();
      DSLContext create = dsl.getCreate();
      if (!url.isEmpty() && !year.isEmpty()) {
        calculateHitStats(create, context, url, year);
      }

    } catch (Exception e) {
      e.printStackTrace();
      context.put("error", e.getMessage());
      jOOQ.onException(dsl);
    } finally {
      jOOQ.after(dsl);
    }
  }

  public static void calculateHitStats(DSLContext dsl, MicroContext context, String url, String year) throws IOException {
    Record u = dsl.fetchOne("select * from urls where id=?", Bijective.decode(url));

    if (u != null) {
      Result<Record> hitStats = dsl.fetch("SELECT" +
          " MONTH(HITS.created_at) as month, count(HITS.created_at) as total_hitcount FROM URLS" +
          "  INNER JOIN HITS ON URLS.ID = HITS.URL_ID and YEAR(HITS.created_at) = ?" +
          "   where URLS.ID = ?" +
          " group by MONTH(HITS.created_at)" +
          " order by MONTH(HITS.created_at) ASC", year, u.getValue(Urls.URLS.ID));
      // TODO: use a DataMapper [Florin]

      List<MicroContext<String>> hitReport = new ArrayList<MicroContext<String>>();
      for (Record hitStat : hitStats) {
        hitReport.add((MicroContext<String>) new MicroContext<String>()
            .with("month", hitStat.getValue("MONTH"))
            .with("total_hitcount", hitStat.getValue("TOTAL_HITCOUNT")));
      }

      context.put("shortUrl", url);
      context.put("originalUrlCreatedAt", new DateTime(u.getValue(Urls.URLS.CREATED_AT)));
      context.put("url", new String(Base64.decode(u.getValue(Urls.URLS.URL)
          .getBytes(Charset.forName(Globals.UTF8)))));

      context.put("year", year);
      context.put("hitStats", hitReport);
    }
  }
}
