package com.example.hartat;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;



public class MainActivity extends FragmentActivity implements
													GooglePlayServicesClient.ConnectionCallbacks,
													GooglePlayServicesClient.OnConnectionFailedListener {
	private MapFragment mapFragment;
	private GoogleMap map;
	private LocationClient mLocationClient;
	
	private final static int CONNECTION_FAILURE_REQUEST_CODE = 9000;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // LocationClient ‘sht‘ per geofence e sherbime tjera...
        mLocationClient = new LocationClient(this, this, this);
		mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
		if (mapFragment != null) {
			map = mapFragment.getMap();
			if (map != null) {
				map.setMyLocationEnabled(true);
			} else {
				Toast.makeText(this, "Harta nuk ekziston!!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Fragmenti nuk ekziston!!", Toast.LENGTH_SHORT).show();
		}
        
    }

    @Override
	protected void onStart() {
		super.onStart();
		if (isGooglePlayServicesAvailable()) {
			mLocationClient.connect();
		}

	}
    
    @Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case CONNECTION_FAILURE_REQUEST_CODE:			
			switch (resultCode) {
			case Activity.RESULT_OK:
				mLocationClient.connect();
				break;
			}

		}
	}

    
    private boolean isGooglePlayServicesAvailable() {
		// Kqyr se a jon Google Play Services te instalume?
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// Nese gjethcka osht n'rregull..
		if (ConnectionResult.SUCCESS == resultCode) {
			return true;
		} else {
			// Merre error Dialog-un prej Play Services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					CONNECTION_FAILURE_REQUEST_CODE);

			// Nese Google Play Services na kthejne dialog
			if (errorDialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();	
				errorFragment.setDialog(errorDialog);
				errorFragment.show(getFragmentManager(), "Location Updates");
			}

			return false;
		}
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Nese nuk munet me u qase ne Google Play Services, gjenero 1 error
		 */
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_REQUEST_CODE);
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"S'ka hart‘ p‘r ty!", Toast.LENGTH_LONG).show();
		}
		
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		Location location = mLocationClient.getLastLocation();
		if (location != null) {
			Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			
			// zoomirate harten 
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
			map.animateCamera(cameraUpdate);
		} else {
			Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	public static class ErrorDialogFragment extends DialogFragment {
		private Dialog mDialog;

		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
}
