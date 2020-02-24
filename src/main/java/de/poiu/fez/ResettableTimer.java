package de.poiu.fez;

import de.poiu.fez.nullaway.Nullable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.poiu.fez.nullaway.NullawayHelper.castToNonNull;

/**
 * A Timer that allows single or periodic execution in the future and has the ability
 * to reset the time for the next execution.
 * <p>
 * To actually start the scheduling, the {@link #reset(boolean)} method must be called.
 * <p>
 * It is possible to restart the scheduling again, even after cancelling it.
 * <p>
 * This class was very much inspired by
 * <a href="https://stackoverflow.com/a/2142661/572645" target="_top">https://stackoverflow.com/a/2142661/572645</a>.
 *
 * @author mherrn
 */
public class ResettableTimer {
  private static final Logger LOGGER= Logger.getLogger(ResettableTimer.class.getName());

  private static enum ScheduleType{
    SCHEDULE,
    AT_FIXED_RATE,
    WITH_FIXED_DELAY,
    ;
  }

  private final ScheduleType scheduleType;
  @Nullable
  private ScheduledExecutorService executorService;
  private final long initialDelay;
  private final long period;
  private final TimeUnit timeUnit;
  private final Runnable task;
  // use AtomicReference to manage concurrency in case reset() gets called from different threads
  private final AtomicReference<ScheduledFuture<?>> futureRef= new AtomicReference();
  @Nullable
  private final String name;


  /**
   * Creates a new ResettableTimer with a SingleThreadScheduledExecutor
   *
   * @param task
   * @param delay
   * @param period
   * @param timeUnit
   * @see ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
   */
  private ResettableTimer(final ScheduleType scheduleType, final long delay, final long period, final TimeUnit timeUnit, final Runnable task) {
    this(scheduleType, delay, period, timeUnit, task, null);
  }


  /**
   * Creates a new ResettableTimer with a SingleThreadScheduledExecutor
   *
   * @param task
   * @param delay
   * @param period
   * @param timeUnit
   * @see ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
   */
  private ResettableTimer(final ScheduleType scheduleType,
                          final long delay,
                          final long period,
                          final TimeUnit timeUnit,
                          final Runnable task,
                          @Nullable final String name) {
    this.scheduleType= scheduleType;
    this.initialDelay= delay;
    this.period= period;
    this.timeUnit= timeUnit;
    this.task= task;
    this.name= name;
  }


  /**
   *
   * @param initialDelay
   * @param period
   * @param timeUnit
   * @param task
   * @return
   * @see ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
   */
  public static ResettableTimer createScheduleAtFixedRate(final long initialDelay, final long period, final TimeUnit timeUnit, final Runnable task){
    return new ResettableTimer(ScheduleType.AT_FIXED_RATE, initialDelay, period, timeUnit, task);
  }


  /**
   *
   * @param initialDelay
   * @param period
   * @param timeUnit
   * @param task
   * @param name
   * @return
   * @see ScheduledExecutorService#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
   */
  public static ResettableTimer createScheduleAtFixedRate(final long initialDelay, final long period, final TimeUnit timeUnit, final Runnable task, final String name){
    return new ResettableTimer(ScheduleType.AT_FIXED_RATE, initialDelay, period, timeUnit, task, name);
  }



  /**
   *
   * @param initialDelay
   * @param delay
   * @param timeUnit
   * @param task
   * @return
   * @see ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
   */
  public static ResettableTimer createScheduleWithFixedDelay(final long initialDelay, final long delay, final TimeUnit timeUnit, final Runnable task){
    return new ResettableTimer(ScheduleType.WITH_FIXED_DELAY, initialDelay, delay, timeUnit, task);
  }


  /**
   *
   * @param initialDelay
   * @param delay
   * @param timeUnit
   * @param task
   * @param name
   * @return
   * @see ScheduledExecutorService#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
   */
  public static ResettableTimer createScheduleWithFixedDelay(final long initialDelay, final long delay, final TimeUnit timeUnit, final Runnable task, final String name){
    return new ResettableTimer(ScheduleType.WITH_FIXED_DELAY, initialDelay, delay, timeUnit, task, name);
  }


  /**
   *
   * @param delay
   * @param timeUnit
   * @param task
   * @return
   * @see ScheduledExecutorService#schedule(java.lang.Runnable, long, java.util.concurrent.TimeUnit)
   */
  public static ResettableTimer createSchedule(final long delay, final TimeUnit timeUnit, final Runnable task){
    return new ResettableTimer(ScheduleType.SCHEDULE, delay, -1, timeUnit, task);
  }


  /**
   *
   * @param delay
   * @param timeUnit
   * @param task
   * @param name
   * @return
   * @see ScheduledExecutorService#schedule(java.lang.Runnable, long, java.util.concurrent.TimeUnit)
   */
  public static ResettableTimer createSchedule(final long delay, final TimeUnit timeUnit, final Runnable task, final String name){
    return new ResettableTimer(ScheduleType.SCHEDULE, delay, -1, timeUnit, task, name);
  }


  /**
   * Starts the timer.
   *
   * @return this ResettableTimer
   * @throws IllegalStateException if the timer was already startet
   */
  public ResettableTimer start() {
    synchronized(this.futureRef){
      final ScheduledFuture<?> oldFuture= this.futureRef.get();
      if (oldFuture != null){
        throw new IllegalStateException("Timer already startet");
      }

      //create ExecutorService
      if (this.executorService == null || this.executorService.isShutdown()) {
        this.executorService= Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(this.toString()+".executorService"));
      } else {
        LOGGER.log(Level.WARNING, "ExecutorService already exists (this is not expected). Not creating a new one.");
      }

      //create and schedule the new future
      switch(scheduleType){
        case SCHEDULE:
          this.futureRef.set(this.executorService.schedule(task, initialDelay, timeUnit));
          break;
        case AT_FIXED_RATE:
          this.futureRef.set(this.executorService.scheduleAtFixedRate(task, initialDelay, period, timeUnit));
          break;
        case WITH_FIXED_DELAY:
          this.futureRef.set(this.executorService.scheduleWithFixedDelay(task, initialDelay, period, timeUnit));
          break;
        default:
          throw new IllegalStateException("Unexpected ScheduleType: "+scheduleType);
      }

      shutdownAfterCompletion(castToNonNull(this.futureRef.get()));
      
    }

    return this;
  }


  /**
   * Starts the timer. This method differs from {@link #start() } in that it
   * does not throw an {@link IllegalStateException} if the timer was already started.
   *
   * @return this ResettableTimer
   */
  public ResettableTimer startIfNotStarted() {
    synchronized(this.futureRef){
      final ScheduledFuture<?> oldFuture= this.futureRef.get();
      if (oldFuture != null){
        //do nothing
        return this;
      }

      //create ExecutorService
      if (this.executorService == null || this.executorService.isShutdown()) {
        this.executorService= Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(this.toString()+".executorService"));
      } else {
        LOGGER.log(Level.WARNING, "ExecutorService already exists (this is not expected). Not creating a new one.");
      }

      //create and schedule the new future
      switch(scheduleType){
        case SCHEDULE:
          this.futureRef.set(this.executorService.schedule(task, initialDelay, timeUnit));
          break;
        case AT_FIXED_RATE:
          this.futureRef.set(this.executorService.scheduleAtFixedRate(task, initialDelay, period, timeUnit));
          break;
        case WITH_FIXED_DELAY:
          this.futureRef.set(this.executorService.scheduleWithFixedDelay(task, initialDelay, period, timeUnit));
          break;
        default:
          throw new IllegalStateException("Unexpected ScheduleType: "+scheduleType);
      }

      shutdownAfterCompletion(castToNonNull(this.futureRef.get()));

    }

    return this;
  }


  /**
   * Resets the timer.
   *
   * @param mayInterruptIfRunning whether to try to interrupt an already running execution
   * @return this ResettableTimer
   */
  public ResettableTimer reset(boolean mayInterruptIfRunning) {
    synchronized(this.futureRef){
      //stop any existing future
      final ScheduledFuture<?> oldFuture= this.futureRef.get();
      if (oldFuture != null){
        oldFuture.cancel(mayInterruptIfRunning);
      }

      //create ExecutorService
      if (this.executorService == null || this.executorService.isShutdown()) {
        this.executorService= Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(this.toString()+".executorService"));
      } else {
        LOGGER.log(Level.FINEST, "ExecutorService already exists. Not creating a new one.");
      }

      //create and schedule the new future
      switch(scheduleType){
        case SCHEDULE:
          this.futureRef.set(this.executorService.schedule(task, initialDelay, timeUnit));
          break;
        case AT_FIXED_RATE:
          this.futureRef.set(this.executorService.scheduleAtFixedRate(task, initialDelay, period, timeUnit));
          break;
        case WITH_FIXED_DELAY:
          this.futureRef.set(this.executorService.scheduleWithFixedDelay(task, initialDelay, period, timeUnit));
          break;
        default:
          throw new IllegalStateException("Unexpected ScheduleType: "+scheduleType);
      }

      shutdownAfterCompletion(castToNonNull(this.futureRef.get()));

    }

    return this;
  }


  /**
   * Cancels the timer.
   *
   * @param mayInterruptIfRunning whether to try to interrupt an already running execution
   * @return this ResettableTimer
   */
  public ResettableTimer cancel(boolean mayInterruptIfRunning){
    synchronized(this.futureRef){
      final ScheduledFuture<?> future= this.futureRef.get();

      if (future != null){
        future.cancel(mayInterruptIfRunning);
      }

      this.futureRef.set(null);
    }

    return this;
  }


  /**
   * Waits in a separate thread for the complection of the specified future and shuts down
   * the execturor service in that case.
   * <p>
   * Completion is assumed, when the scheduled task normally finishes or has been cancelled
   * or has been interrupted or an ExecutionException occurred.
   * <p>
   * ATTENTION! This method is not thread safe! It must be guaranteed by the calling method
   * that no more than 1 future is running at the same time.
   *
   * @param future
   */
  private void shutdownAfterCompletion(final ScheduledFuture future) {
    final Thread shutdownAfterCompletionThread= new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          final Object result = future.get();
          LOGGER.log(Level.FINEST, "Scheduled task has finished");
        } catch (InterruptedException ex) {
          LOGGER.log(Level.WARNING, "Scheduled task has been interrupted", ex);
        } catch (ExecutionException ex) {
          LOGGER.log(Level.WARNING, "Scheduled task resulted in Exception", ex);
        } catch (CancellationException ex) {
          LOGGER.log(Level.FINEST, "Scheduled task has been cancelled", ex);
        }


        LOGGER.log(Level.FINER, "Shutdown ExecutorService, since scheduled task has finished (or has been cancelled).");
        synchronized(futureRef) {
          if (executorService != null) {
            executorService.shutdown();
            executorService= null;
          }
        }
      }
    }, this.toString()+".shutdownAfterCompletion");
    shutdownAfterCompletionThread.setDaemon(true);
    shutdownAfterCompletionThread.start();
  }


  @Override
  public String toString() {
    return "ResettableTimer" + (this.name != null ? "-" + this.name : "@" + Integer.toHexString(System.identityHashCode(this)));
  }

}