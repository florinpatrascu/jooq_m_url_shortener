package controllers;

import ca.simplegames.micro.Controller;
import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import ca.simplegames.micro.SiteContext;
import ca.simplegames.micro.controllers.ControllerException;
import ca.simplegames.micro.extensions.JOOQExtension;
import ca.simplegames.micro.utils.Assert;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import controllers.utils.Bijective;
import models.Tables;
import models.tables.Hits;
import models.tables.Urls;
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jrack.JRack;
import org.jrack.RackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Main controller responsible for finding and creating short URLs
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2014-03-30)
 */
public class MicroUrlController implements Controller {
  public static final Logger log = LoggerFactory.getLogger(MicroUrlController.class);

  enum Methods {POST, DELETE, PUT, HEAD, GET}


  public void execute(MicroContext context, Map map) throws ControllerException {
    @SuppressWarnings(value = "unchecked")
    Map<String, Object> params = (Map<String, Object>) context.get(Globals.PARAMS);
    SiteContext site = context.getSiteContext();
    JOOQExtension jOOQ = (JOOQExtension) site.get("jOOQ");
    Assert.notNull(jOOQ, "Please install the jOOQ extension.");

    String url = (String) params.get("url");
    url = url == null || url.isEmpty() ? Globals.EMPTY_STRING : url;
    JOOQExtension.MDSL dsl = null;

    try {
      dsl = jOOQ.getDSL();
      DSLContext create = dsl.getCreate();

      switch (Methods.valueOf((String) context.getRackInput().get(JRack.REQUEST_METHOD))) {
        case POST:
          if (!url.isEmpty()) {
            List<Pattern> domainRestrictionPatterns = (List<Pattern>) site.get("domainRestrictionPatterns");
            String error = Globals.EMPTY_STRING;
            if (domainRestrictionPatterns != null && !domainRestrictionPatterns.isEmpty()) {
              boolean matches = false;
              for (Pattern domainRestrictionPattern : domainRestrictionPatterns) {
                matches = domainRestrictionPattern.matcher(url).matches();
                if (matches) break;
              }

              if (!matches) {
                error = "Domain restrictions apply, sorry.";
              }
            }

            if (error.isEmpty()) {
              String hashedurl = Base64.encode(url.getBytes(Charset.forName(Globals.UTF8)));
              Record u = create.select().from(Tables.URLS).where("url=?", hashedurl).fetchOne();

              if (u == null) {
                u = create.insertInto(Urls.URLS)
                    .set(Urls.URLS.URL, hashedurl)
                    .set(Urls.URLS.CREATED_AT, new Timestamp(DateTime.now().getMillis()))
                    .returning().fetchOne();
                log.info(String.format("Created a new URL record. ID: %d", u.getValue(Urls.URLS.ID)));
              }

              HitStatsController.calculateHitStats(create, context,
                  Bijective.encode(u.getValue(Urls.URLS.ID)), DateTime.now().toString("yyyy"));

              context.put("shortUrl", Bijective.encode(u.getValue(Urls.URLS.ID)));
            } else {
              context.with("error", error);
            }
          }
          break;

        case DELETE: // delete... really? Should anybody be able to delete a µURL?
          if (!url.isEmpty()) {
            Record u = create.select().from(Tables.URLS).where("url=?", Bijective.decode(url)).fetchOne();
            if (u != null) {
              create.executeDelete((org.jooq.UpdatableRecord) u);
              // how do I cascade delete to erase the related Hit records too?!
            }
          }
          context.halt();
          break;

        default: // GET => list
          if (!url.isEmpty()) {
            Record u = create.resultQuery("select * from urls where id=?", Bijective.decode(url)).fetchOne();

            if (u != null) {
              create.insertInto(Hits.HITS, Hits.HITS.URL_ID)
                  .values(u.getValue(Urls.URLS.ID)).returning().fetchOne();

              int rCode = !"GET".equalsIgnoreCase(context.getRequest().getMethod()) ? 303 : 302;

              RackResponse response = new RackResponse(rCode)
                  .withBody(Globals.EMPTY_STRING)
                  .withContentLength(0);

              context.with(Globals.RACK_RESPONSE, response
                  .withHeader("Location", new String(Base64.decode(u.getValue(Urls.URLS.URL)
                      .getBytes(Charset.forName(Globals.UTF8))))));

              context.halt();
            }
          }
      }
    } catch (Exception e) {
      e.printStackTrace();
      jOOQ.onException(dsl);
      context.put("error", e.getMessage());
    } finally {
      jOOQ.after(dsl);
    }
  }
}
