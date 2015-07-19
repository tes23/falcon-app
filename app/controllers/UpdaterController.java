package controllers;

import actors.PubSubParentActor;
import actors.PubSubParentActorProtocol;
import actors.MessageBroadcasterActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.UUID;

import static actors.PubSubParentActorProtocol.ActorNamePath.*;

@Singleton
public class UpdaterController extends Controller {

    @Inject @Named(PUB_SUB_PARENT)
    private ActorRef actorRef;

    public Result index() {
        return ok(index.render());
    }

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

    // get the ws.js script
    public Result wsJs() {
        return ok(views.js.ws.render());
    }

    // Websocket interface
    public WebSocket<String> wsInterface(){
        return new WebSocket<String>() {

            @Override
            public void onReady(In<String> in, Out<String> out) {
                try {
                    MessageBroadcasterActor.registerClient(UUID.randomUUID().toString(), in, out);
                } catch (Exception e) {
                    //TODO handle exceptions
                    e.printStackTrace();
                    out.close();
                }
            }
        };
    }
}
