/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.libs.streams;

import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.*;
import java.util.function.Function;
import scala.runtime.AbstractFunction1;

/**
 * Provides a flow that is handled by an actor.
 *
 * <p>See https://github.com/akka/akka/issues/16985.
 */
public class ActorFlow {

  /**
   * Create a flow that is handled by an actor.
   *
   * <p>Messages can be sent downstream by sending them to the actor passed into the props function.
   * This actor meets the contract of the actor returned by {@link
   * akka.stream.javadsl.Source#actorRef}.
   *
   * <p>The props function should return the props for an actor to handle the flow. This actor will
   * be created using the passed in {@link akka.actor.ActorRefFactory}. Each message received will
   * be sent to the actor - there is no back pressure, if the actor is unable to process the
   * messages, they will queue up in the actors mailbox. The upstream can be cancelled by the actor
   * terminating itself.
   *
   * @param <In> the In type parameter for a Flow
   * @param <Out> the Out type parameter for a Flow
   * @param props A function that creates the props for actor to handle the flow.
   * @param bufferSize The maximum number of elements to buffer.
   * @param overflowStrategy The strategy for how to handle a buffer overflow.
   * @param factory The Actor Factory used to create the actor to handle the flow - for example, an
   *     ActorSystem.
   * @param mat The materializer to materialize the flow.
   * @return the flow itself.
   */
  public static <In, Out> Flow<In, Out, ?> actorRef(
      Function<ActorRef, Props> props,
      int bufferSize,
      OverflowStrategy overflowStrategy,
      ActorRefFactory factory,
      Materializer mat) {

    return play.api.libs.streams.ActorFlow.<In, Out>actorRef(
            new AbstractFunction1<ActorRef, Props>() {
              @Override
              public Props apply(ActorRef v1) {
                return props.apply(v1);
              }
            },
            bufferSize,
            overflowStrategy,
            factory,
            mat)
        .asJava();
  }

  /**
   * Create a flow that is handled by an actor.
   *
   * <p>Messages can be sent downstream by sending them to the actor passed into the props function.
   * This actor meets the contract of the actor returned by {@link
   * akka.stream.javadsl.Source#actorRef}, defaulting to a buffer size of 16, and failing the stream
   * if the buffer gets full.
   *
   * <p>The props function should return the props for an actor to handle the flow. This actor will
   * be created using the passed in {@link akka.actor.ActorRefFactory}. Each message received will
   * be sent to the actor - there is no back pressure, if the actor is unable to process the
   * messages, they will queue up in the actors mailbox. The upstream can be cancelled by the actor
   * terminating itself.
   *
   * @param <In> the In type parameter for a Flow
   * @param <Out> the Out type parameter for a Flow
   * @param props A function that creates the props for actor to handle the flow.
   * @param factory The Actor Factory used to create the actor to handle the flow - for example, an
   *     ActorSystem.
   * @param mat The materializer to materialize the flow.
   * @return the flow itself.
   */
  public static <In, Out> Flow<In, Out, ?> actorRef(
      Function<ActorRef, Props> props, ActorRefFactory factory, Materializer mat) {

    return play.api.libs.streams.ActorFlow.<In, Out>actorRef(
            new AbstractFunction1<ActorRef, Props>() {
              @Override
              public Props apply(ActorRef v1) {
                return props.apply(v1);
              }
            },
            16,
            OverflowStrategy.fail(),
            factory,
            mat)
        .asJava();
  }
}
