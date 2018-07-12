package org.zaproxy.zap.extension.websocket.pscan;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class WebSocketPassiveScannerList {
    
    private static final Logger LOGGER = Logger.getLogger(WebSocketPassiveScannerList.class);
    
    private LinkedHashSet<WebSocketPassiveScanner> passiveScanners;
    private boolean isIteratorUpdated ;
    private Iterator<WebSocketPassiveScanner> iterator;

    public WebSocketPassiveScannerList(){
        passiveScanners = new ConcurrentLinkedHashSet<>();
        isIteratorUpdated = false;
    }
    
    public boolean add(WebSocketPassiveScanner passiveScanner){
        if(passiveScanners.contains(passiveScanner.getName())){
            LOGGER.warn("Insertion prevent in order to avoid the duplication");
            return false;
        }
        passiveScanners.add(passiveScanner);
        isIteratorUpdated = false;
        return true;
    }
    
    
    
    public WebSocketPassiveScanner removeScanner(String className) {
        
        Iterator<WebSocketPassiveScanner> iterator = getIterator();
        WebSocketPassiveScanner scanner;
        while ( iterator.hasNext()) {
            scanner = iterator.next();
            if (scanner.getClass().getName().equals(className)) {
                passiveScanners.remove(scanner);
                isIteratorUpdated = false;
                return scanner;
            }
        }
        return null;
    }
    
    public Iterator<WebSocketPassiveScanner> getIterator(){
        if(!isIteratorUpdated){
            iterator = passiveScanners.iterator();
        }
        return iterator;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * operations such as contains() and isEmpty() may see stale values since they are not properly synchronized.
     *
     * @author Abhishek.Bhatia
     *
     * @param <E>
     */
    public class ConcurrentLinkedHashSet<E> extends LinkedHashSet<E> implements Set<E>
    {
        /**
         *
         */
        private static final long serialVersionUID = 3613833793983575105L;
        private boolean isUpdating = false;
        
        
        /**
         * This method returns the size of the Set.
         *
         * @return : The size of the set. It returns -1, if the thread has been interrupted.
         */
        @Override
        public synchronized int size()
        {
            while (isUpdating)
            {
                try
                {
                    wait();
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    return -1;
                }
            }
            isUpdating=true;//setting true so that proper size at that time is returned, i.e. no one updates the set while calculating its size.
            int size= super.size();
            isUpdating=false;
            notifyAll();
            return size;
        }
        
        @Override
        public synchronized boolean addAll(Collection<? extends E> arg0)
        {
            while (isUpdating)
            {
                try
                {
                    wait();
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    return false;
                }
            }
            isUpdating = true;
            boolean result = false;
            result = super.addAll(arg0);
            isUpdating = false;
            notifyAll();
            return result;
        }
        
        @Override
        public synchronized boolean add(E arg0)
        {
            while (isUpdating)
            {
                try
                {
                    wait();
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    return false;
                }
            }
            isUpdating = true;
            boolean result = super.add(arg0);
            isUpdating = false;
            notifyAll();
            return result;
        }
        
        @Override
        public boolean remove(Object arg0)
        {
            while (isUpdating)
            {
                try
                {
                    wait();
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    return false;
                }
            }
            isUpdating = true;
            boolean result = super.remove(arg0);
            isUpdating = false;
            notifyAll();
            return result;
        }
        
        @Override
        public boolean removeAll(Collection<?> arg0)
        {
            while (isUpdating)
            {
                try
                {
                    wait();
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    return false;
                }
            }
            isUpdating = true;
            boolean result = super.removeAll(arg0);
            isUpdating = false;
            notifyAll();
            return result;
        }
    }
}
