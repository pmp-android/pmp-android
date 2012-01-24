package de.unistuttgart.ipvs.pmp.api.ipc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.os.IBinder;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.api.ipc.command.IPCCommand;

/**
 * The API IPC Scheduler to queue IPC commands and execute them.
 * 
 * @author Tobias Kuhn
 * 
 */
public class IPCScheduler extends Thread {
    
    /**
     * The queue that contains all the commands to be executed
     */
    private final BlockingQueue<IPCCommand> queue;
    
    /**
     * The context to use for the connection
     */
    private final Context context;
    
    /**
     * The connection to be used while executing commands.
     */
    private IPCConnection connection;
    
    
    /**
     * Creates a new {@link IPCScheduler}.
     * 
     * @param context
     *            the context to use for the connections
     */
    public IPCScheduler(Context context) {
        super("IPC Scheduling");
        this.queue = new LinkedBlockingQueue<IPCCommand>();
        this.context = context;
        start();
    }
    
    
    /**
     * Queues a new {@link IPCCommand} to be executed.
     * 
     * @param command
     *            the IPC command to be executed at an arbitrary point in the future
     */
    public void queue(IPCCommand command) {
        if (!this.queue.offer(command)) {
            Log.e("BlockingQueue ran out of capacity!");
        }
    }
    
    
    @Override
    public void run() {
        // must be initialized in a different thread
        this.connection = new IPCConnection(this.context);
        
        while (!isInterrupted()) {
            
            try {
                final IPCCommand command = this.queue.take();
                
                new Thread() {
                    
                    @Override
                    public void run() {
                        IPCScheduler.this.connection.setDestinationService(command.getDestinationService());
                        
                        // handle timeout
                        if (command.getTimeout() < System.currentTimeMillis()) {
                            command.getHandler().onTimeout();
                            return;
                        }
                        
                        command.getHandler().onPrepare();
                        
                        // try connecting
                        IBinder binder = IPCScheduler.this.connection.getBinder();
                        if (binder != null) {
                            command.execute(binder);
                        } else {
                            command.getHandler().onBindingFailed();
                        }
                        
                        command.getHandler().onFinalize();
                        
                    };
                }.start();
                
            } catch (InterruptedException e) {
                continue;
            }
            
        }
    }
}