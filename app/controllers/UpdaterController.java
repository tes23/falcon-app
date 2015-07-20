package controllers;

import actors.MessageBroadcasterActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import redis.ChannelMessage;
import views.html.index;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.UUID;

import static actors.ConsumerActorProtocol.ActorNamePath.CONSUMER;

@Singleton
public class UpdaterController extends Controller {

    @Inject @Named(CONSUMER)
    private ActorRef consumerActor;

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

                    }
                }, null
        );*/

        ChannelMessage message = new ChannelMessage(jsonNode.toString());
        consumerActor.tell(message, null);
//        return F.Promise.wrap(Patterns.ask(actorRef, message, 30000)).map(response -> ok((String) response));
        return ok("Message received!");
    }

    public F.Promise<Result> sayhello(String name) {
        ChannelMessage message = new ChannelMessage(name);

        return F.Promise.wrap(Patterns.ask(consumerActor, message, 30000)).map(response -> ok((String) response));
    }

    /** Get the ws.js script */
    public Result wsJs() {
        return ok(views.js.ws.render());
    }

    /** Websocket interface */
    public WebSocket<String> wsInterface(){
        return WebSocket.whenReady((in, out) -> {
            try {
                MessageBroadcasterActor.registerClient(UUID.randomUUID().toString(), in, out);
            } catch (Exception e) {
                //TODO handle exceptions
                e.printStackTrace();
                out.close();
            }
        });
    }
}
