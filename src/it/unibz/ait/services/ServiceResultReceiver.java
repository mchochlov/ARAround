package it.unibz.ait.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ServiceResultReceiver extends ResultReceiver {

	private Receiver mReceiver;

    public ServiceResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }
    
    public Receiver getReceiver() {
        return this.mReceiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
