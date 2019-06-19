package com.spbu.bigdatatasks.task2;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static akka.pattern.Patterns.ask;

public class AkkaActorsFactorsCounter  extends FactorsCounter {

    private final int workersAmount;

    public AkkaActorsFactorsCounter(int workersAmount) {
        this.workersAmount = workersAmount;
    }
    public AkkaActorsFactorsCounter() {
        this(Runtime.getRuntime().availableProcessors() * 4);
    }

    // messages
    static public class Start {
        public Start() {}
    }

    static public class Work {
        public final Long pieceOfWork;
        public Work(Long pieceOfWork) {
            this.pieceOfWork = pieceOfWork;
        }
    }

    static public class CompletedWork {
        public final int localResult;
        public CompletedWork(int localResult) {
            this.localResult = localResult;
        }
    }

    static public class IsDone {
        public IsDone() {}
    }

    static public class Result {
        public Optional<Integer> result;
        public Result(Optional<Integer> result) {
            this.result = result;
        }
    }

    /**
     * This class receives the numbers and then dispatches them to his children
     * Also it tracks when everything is done by keeping track of iterator and how many children are currently
     * processing something
     */
    static class WorkDispatcher extends AbstractActor {

        private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
        private Iterator<Long> data;
        private List<ActorRef> children;
        private int result;
        private int in_progress;
        public boolean done;

        private Optional<Long> next() {
            if (data.hasNext()) {
                return Optional.of(data.next());
            }
            else {
                return Optional.empty();
            }
        }

        // Helper to create actor
        static Props props(List<Long> data, int workers) {
            return Props.create(WorkDispatcher.class, () -> new WorkDispatcher(data, workers));
        }

        WorkDispatcher(Iterable<Long> data, int workers) {
            this.data = data.iterator();
            this.result = 0;
            this.in_progress = 0;
            this.children = new ArrayList<>();
            this.done = false;
            for (int i = 0; i < workers; i++) {
                this.children.add(getContext().actorOf(FactorizeActor.props(), "child" + i));
            }
        }

        // message handling
        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(
                            Start.class,
                            msg -> {
                                //log.info("Starting");
                                for (ActorRef child : children) {
                                    Optional<Long> piece_of_work = next();
                                    if (piece_of_work.isPresent()) {
                                        child.tell(new Work(piece_of_work.get()), getSelf());
                                        in_progress++;
                                    }
                                }
                            })
                    .match(
                            CompletedWork.class,
                            msg -> {
                                //log.info("Received local result" + msg.localResult);
                                result += msg.localResult;
                                in_progress--;

                                Optional<Long> piece_of_work = next();
                                if (piece_of_work.isPresent()) {
                                    getSender().tell(new Work(piece_of_work.get()), getSelf());
                                    in_progress++;
                                } else if (in_progress == 0) {
                                    //log.info("Completed " + result);
                                    this.done = true;
                                }
                            })
                    .match(
                            IsDone.class,
                            msg -> {
                                if (this.done) {
                                    getSender().tell(new Result(Optional.of(result)), getSelf());
                                } else {
                                    getSender().tell(new Result(Optional.empty()), getSelf());
                                }
                            })
                    .build();
        }
    }

    /**
     * This class only knows how to factorize and return the answer to the parent
     */
    static class FactorizeActor extends AbstractActor {

        static Props props() {
            return Props.create(FactorizeActor.class, () -> new FactorizeActor());
        }
        private static final Logger log = LoggerFactory.getLogger(FactorizeActor.class);

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(
                            Work.class,
                            w -> {
                                List<Long> factors = PrimeFactors.factorize(w.pieceOfWork);
                                log.info("Number: " + Long.toUnsignedString(w.pieceOfWork) + " factors: " + factors);
                                getSender().tell(new CompletedWork(factors.size()), getSelf());
                            })
                    .build();
        }
    }

    @Override
    public long readAndCount(String path) {
        List<Long> numbers = readAllNumbers(path);

        ActorSystem system = ActorSystem.create("actor-system");
        ActorRef workDispatcherRef = system.actorOf(WorkDispatcher.props(numbers, workersAmount));

        workDispatcherRef.tell(new Start(), ActorRef.noSender());

        Optional<Integer> result = Optional.empty();
        while (!result.isPresent()) {
            CompletableFuture<Object> future =
                    ask(workDispatcherRef, new IsDone(), Duration.ofMillis(1000)).toCompletableFuture();

            try {
                result = ((Result)future.get()).result;
            } catch (Exception e) {
                log.info("Something's is wrong...\n" + e.getMessage());
            }
        }
        long finalResult = result.get();
        system.terminate();

        return finalResult;
    }
}