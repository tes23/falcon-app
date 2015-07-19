import play.GlobalSettings;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

public class Global extends GlobalSettings {

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader requestHeader) {
        return F.Promise.<Result>pure(Results.notFound(
                views.html.defaultpages.notFound.render(requestHeader.method(), requestHeader.uri())));
    }

    @Override
    public F.Promise<Result> onBadRequest(Http.RequestHeader requestHeader, String error) {
        return F.Promise.<Result>pure(Results.badRequest(
                views.html.defaultpages.badRequest.render(requestHeader.method(), requestHeader.uri(), error)));
    }
}
