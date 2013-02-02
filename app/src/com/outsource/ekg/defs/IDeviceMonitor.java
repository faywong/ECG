package com.outsource.ekg.defs;

import java.util.Set;

public interface IDeviceMonitor {
	public static final int DEVICE_TYPE_ALL = 0;
	public static final int DEVICE_TYPE_SERVER = 1;
	public static final int DEVICE_TYPE_CLINET = 2;

	public Set<IEKGSever> getAvailableServers();

	public Set<IEKGClient> getAvailableClients();

	public boolean registerDeviceInfoListener(int type,
			IDeviceInfoListener listener);
	
	public static interface IDeviceInfoListener {
		public enum EVT_TYPE {
			// bluetooth specific
			EVT_NEW_DEVICE_FOUND,
			EVT_DEVICE_CLASS_CHANGED,
			EVT_DEVICE_NAME_CHANGED,
			EVT_DEVICE_BOUDED,
			EVT_DEVICE_BOUDING,
			EVT_DEVICE_BOUND_STATE_CHANGED,
			// for extension
			EVT_USER1,
			EVT_USER2,
			EVT_USER3
		};
		
		public void onEvent(EVT_TYPE type, Object context);
	}
}
