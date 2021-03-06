package org.isel.jingle.view;

import htmlflow.HtmlView;
import htmlflow.StaticHtml;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.vertx.core.http.HttpServerResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.isel.jingle.WebApp;
import org.isel.jingle.model.Album;
import org.isel.jingle.model.Track;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Table;
import org.xmlet.htmlapifaster.Tbody;

public class AlbumsTracksView implements View<Observable<Track>> {

    @Override
    public void write(HttpServerResponse resp, Observable<Track> model) {
        resp.setChunked(true);
        resp.putHeader("content-type", "text/html");
        model.subscribeWith(new Observer<>() {
            Tbody<Table<Body<Html<HtmlView>>>> tbody;
            @Override
            public void onSubscribe(Disposable d) { tbody = header(resp);}

            @Override
            public void onNext(Track track) { body(tbody,track);}

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() {
                tbody.__().__().__().__(); //ends the elements
                resp.end();
            }
        });
    }

    private Tbody<Table<Body<Html<HtmlView>>>> header(HttpServerResponse resp) {
        return StaticHtml.view(new ResponsePrintStream(resp))
                .html()
                    .head()
                        .meta()
                            .attrCharset("UTF-8")
                        .__()
                        .style()
                            .text(WebApp.getStyle())
                        .__()
                        .title()
                            .text("Tracks")
                        .__()
                    .__()
                    .body()
                        .h1()
                            .text("Tracks")
                        .__()
                        .table()
                            .thead()
                                .tr()
                                    .th().text("Name").__()
                                    .th().text("Url").__()
                                    .th().text("Duration").__()
                            .__()
                        .__()
                    .tbody();
    }

    private void body(Tbody<Table<Body<Html<HtmlView>>>> tbody,
            Track track) {
        tbody
                .tr()
                .td().text(track.getName()).__()
                .td()
                    .a()
                        .attrHref(track.getUrl())
                            .text(track.getUrl()).__().__()
                .td().text(track.getDuration()).__()
                .__();
    }

    @Override
    public void write(HttpServerResponse resp) {
        throw new UnsupportedOperationException("This view does not require a Model. You should invoke write(resp) instead!");
    }

    private class ResponsePrintStream extends PrintStream {

        public ResponsePrintStream(HttpServerResponse resp) {
            super(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    char c = (char) b;
                    resp.write(String.valueOf(c));
                }
            });
        }
    }
}
