package controllers;

import actors.PubSubParentActor;
import actors.PubSubParentActorProtocol;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class UpdaterController extends Controller {

    @Inject @Named(PubSubParentActor.NAME)
    private ActorRef actorRef;

    public Result addData() {
        JsonNode jsonNode = request().body().asJson();
        if (jsonNode == null) {
            return badRequest("Wrong data!");
        }

        /*Akka.system().scheduler().scheduleOnce(
                Duration.create(10, TimeUnit.SECONDS),
                new Runnable() {
                    public void run() {
                        System.out.println("ASD");
                    }
                }, null
        );*/

        return ok("Message received!");
    }

    public F.Promise<Result> sayhello(String name) {
        PubSubParentActorProtocol.Message message = new PubSubParentActorProtocol.Message(name);

        //TODO: here we should use tell, which means we don't want to wait for any answer
        return F.Promise.wrap(Patterns.ask(actorRef, message, 30000)).map(response -> ok((String) response));
    }
}
