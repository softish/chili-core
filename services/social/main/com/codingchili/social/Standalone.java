package com.codingchili.social;

import com.codingchili.common.Strings;
import com.codingchili.core.configuration.ServiceConfigurable;
import com.codingchili.core.context.CoreException;
import com.codingchili.core.context.ServiceContext;
import com.codingchili.core.protocol.AbstractHandler;
import com.codingchili.core.protocol.Access;
import com.codingchili.core.protocol.ClusterNode;
import com.codingchili.core.protocol.Protocol;
import com.codingchili.core.protocol.Request;
import com.codingchili.core.protocol.RequestHandler;
import com.codingchili.social.configuration.SocialContext;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * Created by robdu on 2017-03-01.
 */
public class Standalone extends ClusterNode
{
    public class SocialHandler<T extends SocialContext> extends AbstractHandler<T>
    {
        private final Protocol<RequestHandler<Request>> protocol = new Protocol<>();

        public SocialHandler(T context) {
            super(context, Strings.SOCIAL_NODE);

            protocol.use(Strings.ID_PING, Request::accept, Access.PUBLIC)
                    .use(Strings.ID_LICENSE, this::messageHandler, Access.PUBLIC);
        }

        private void messageHandler(Request request) {
            request.write(context.message());
        }

        @Override
        public void handle(Request request) throws CoreException
        {
            protocol.get(request.route()).handle(request);
        }
    }

    @Override
    public void start(Future<Void> start) {
        SocialContext context = new SocialContext(vertx);

        for (int i = 0; i < settings.getHandlers(); i++) {
            context.deploy(new SocialHandler<>(context));
        }
        start.complete();
    }

    public class Application extends ServiceContext {

        protected Application(Vertx vertx)
        {
            super(vertx);
        }

        @Override
        protected ServiceConfigurable service()
        {
            return null;
        }
    }
}
