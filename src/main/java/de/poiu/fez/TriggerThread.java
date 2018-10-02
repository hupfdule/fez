package de.poiu.fez;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Ein Trigger mit Thread zur Ausführung einer vorgegebenen Aktion bei jedem
 * Auslösen des Triggers.
 * <p>
 * Nach Starten des Threads mit {@link #start()} wartet dieser auf Trigger-Events
 * und ruft bei jedem Auslösen das übergebene Runnable aus.
 *
 * @author mherrn
 */
//FIXME: This class isn't tested well. If tested and working make it public
class TriggerThread {
  private static final Logger LOGGER= Logger.getLogger(TriggerThread.class.getName());

  /////////////////////////////////////////////////////////////////////////////
  //
  // Attributes
  /** Der eigentliche Trigger. */
  private final Trigger trigger= new Trigger();

  /** Wird auf true gesetzt, wenn ein Stoppen des Threads gewünscht ist. */
  private boolean stopRequested= false;

  /** Das Runnable das beim Triggern aufgerufen wird. */
  private final Runnable triggerListener;
  /** Der Name dieses Threads. */
  private final String threadName;

  /** Das Runnable, das auf den Eingang von Trigger-Events wartet und bei Eintreffen den {@link #triggerListener} aufruft. */
  private final Runnable triggerThreadRunnable= new Runnable() {
    @Override
    public void run() {
      LOGGER.log(Level.FINE, "Thread {0} is started", threadName);
      while (!stopRequested) {
        try {
          LOGGER.log(Level.FINEST, "Thread {0} is waiting for trigger", threadName);
          trigger.waitForTrigger();
          //wurde beim Warten auf den Trigger das Beenden verlangt, beende hier
          if (stopRequested) {
            return;
          }

          LOGGER.log(Level.FINEST, "Thread {0} was triggered", threadName);

          //führe die eigentliche Aktion aus
          triggerListener.run();
        } catch (InterruptedException ex) {
          LOGGER.log(Level.FINE, "Thread {0} was interrupted", threadName);
        }
      }
      LOGGER.log(Level.FINE, "Thread {0} has ended", threadName);
    }
  };
  private Thread triggerThread;


  /////////////////////////////////////////////////////////////////////////////
  //
  // Constructors
  /**
   * Erzeugt einen neuen TriggerThread.
   * @param runnable das Runnable, das beim Auslösen des Triggers ausgeführt werden soll
   * @param threadName der Name, der diesem Thread gegeben werden soll
   * @param startImmediately gibt an, ob dieser Thread sofort automatisch gestartet werden soll (anstatt erst nach Aufruf von {@link #start() }
   */
  public TriggerThread(final Runnable runnable, final String threadName, final boolean startImmediately) {
    this.triggerListener= runnable;
    this.threadName= threadName;

    if (startImmediately) {
      this.start();
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  //
  // Methods
  /**
   * Erzeugt einen neuen Abarbeitungsthread.
   * @return
   */
  private Thread createNewThread() {
    final Thread thread= new Thread(triggerThreadRunnable, threadName);
    thread.setDaemon(true);
    return thread;
  }


  /**
   * Startet diesen Thread.
   * @throws IllegalStateException wenn dieser Thread bereits gestartet ist
   */
  public void start() {
    synchronized(this) {
      if (this.triggerThread != null && this.triggerThread.isAlive()) {
        throw new IllegalStateException("Thread is already running");
      }

      LOGGER.log(Level.FINER, "Start of Thread {0} requested", threadName);

      this.stopRequested= false;
      this.triggerThread= this.createNewThread();
      this.triggerThread.start();
    }
  }


  /**
   * Stoppt diesen Thread.
   */
  public void stop() {
    synchronized(this) {
      LOGGER.log(Level.FINER, "Stop of Thread {0} requested", threadName);

      this.stopRequested= true;
      this.trigger.trigger();
      this.triggerThread= null;
    }
  }


  /**
   * Triggert den Trigger.
   */
  public void trigger() {
    this.trigger.trigger();
  }
}
