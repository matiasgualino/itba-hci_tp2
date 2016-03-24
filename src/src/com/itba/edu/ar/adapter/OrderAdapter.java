package com.itba.edu.ar.adapter;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.itba.edu.ar.R;
import com.itba.edu.ar.model.Order;





public class OrderAdapter extends ArrayAdapter<Order> {

	private Activity activity;
	private List<Order> items;
	private Order objBean;
	private int row;
	

	public OrderAdapter(Activity act, int resource, List<Order> arrayList)  {
		super(act, resource, arrayList);
		this.activity = act;
		this.row = resource;
		this.items = arrayList;

	}

	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(row, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if ((items == null) || ((position + 1) > items.size()))
			return view;

		objBean = items.get(position);

		holder.tvStatus = (TextView) view.findViewById(R.id.status);
		holder.tvAddres = (TextView) view.findViewById(R.id.addres);
		holder.tvDelivered = (TextView) view.findViewById(R.id.deliveredDate);
		holder.tvLatitude = (TextView) view.findViewById(R.id.latitude);
		holder.tvLongitude = (TextView) view.findViewById(R.id.longitude);
		holder.tvProccesed = (TextView) view.findViewById(R.id.proccesedDate);
		holder.tvReceived = (TextView) view.findViewById(R.id.receivedDate);
		holder.tvShipped = (TextView) view.findViewById(R.id.shippedDate);
		holder.tvId = (TextView) view.findViewById(R.id.id_order);
		
		
		if (holder.tvId != null && null != objBean.getId()
				&& objBean.getStatus().toString().trim().length() > 0) {
			holder.tvId.setText( getContext().getString(R.string.id) + ":" + objBean.getId().toString());
		}
		else
			holder.tvId.setText( getContext().getString(R.string.id) + ":");
		if (holder.tvStatus != null && null != objBean.getStatus()
				&& objBean.getStatus().toString().trim().length() > 0) {
			if(objBean.getStatus().equals("1"))
				holder.tvStatus.setText( getContext().getString(R.string.status) + ":" + "Creada");
			else if (objBean.getStatus().equals("2")){
				holder.tvStatus.setText( getContext().getString(R.string.status) + ":" + "Confirmada");
			}
			else if (objBean.getStatus().equals("3")){
				holder.tvStatus.setText( getContext().getString(R.string.status) + ":" + "Transportada");
			}
			else if (objBean.getStatus().equals("4")){
				holder.tvStatus.setText( getContext().getString(R.string.status) + ":" + "Entregada");
			}
			else{
				holder.tvStatus.setText( getContext().getString(R.string.status) + ":" + "Error en el estado");
			}
		}
		else
			holder.tvStatus.setText( getContext().getString(R.string.status) + ":");
		if (holder.tvShipped != null && null != objBean.getShippedDate()
				&& objBean.getShippedDate().toString().trim().length() > 0) {
			holder.tvShipped.setText(getContext().getString(R.string.shippedDate) + ":" + objBean.getShippedDate());
		}
		else
			holder.tvShipped.setText(getContext().getString(R.string.shippedDate) + ":");
		if (holder.tvReceived != null && null != objBean.getReceivedDate()
				&& objBean.getReceivedDate().toString().trim().length() > 0) {
			holder.tvReceived.setText(getContext().getString(R.string.receivedDate) + ":" + objBean.getReceivedDate());
		}
		else
			holder.tvReceived.setText(getContext().getString(R.string.receivedDate) + ":" );

		if (holder.tvProccesed != null && null != objBean.getProcessedDate()
				&& objBean.getProcessedDate().toString().trim().length() > 0) {
			holder.tvProccesed.setText(getContext().getString(R.string.processedDate) + ":" + objBean.getProcessedDate());
		}
		else
			holder.tvProccesed.setText(getContext().getString(R.string.processedDate) + ":");
		if (holder.tvLongitude != null && null != objBean.getLongitude()
				&& objBean.getLongitude().toString().trim().length() > 0) {
			holder.tvLongitude.setText(getContext().getString(R.string.longitude) + ":" + objBean.getLongitude().toString());
		}
		else
			holder.tvProccesed.setText(getContext().getString(R.string.processedDate) + ":");
		if (holder.tvLatitude != null && null != objBean.getLatitude()
				&& objBean.getLatitude().toString().trim().length() > 0) {
			holder.tvLatitude.setText(getContext().getString(R.string.latitude) + ":" + objBean.getLatitude().toString());
		}
		else
			holder.tvLatitude.setText(getContext().getString(R.string.latitude) + ":");
		if (holder.tvDelivered != null && null != objBean.getDeliveredDate()
				&& objBean.getDeliveredDate().toString().trim().length() > 0) {
			holder.tvDelivered.setText(getContext().getString(R.string.deliveredDate) + ":" + objBean.getDeliveredDate());
		}
		else
			holder.tvDelivered.setText(getContext().getString(R.string.deliveredDate) + ":");
		if (holder.tvAddres != null && null != objBean.getAddres()
				&& objBean.getAddres().toString().trim().length() > 0) {
			holder.tvAddres.setText(getContext().getString( R.string.addres) + ":" + objBean.getAddres());
		}
		else
			holder.tvAddres.setText(getContext().getString( R.string.addres) + ":");
	

		return view;
	}

	public class ViewHolder {

		public TextView tvStatus, tvAddres, tvProccesed,tvLatitude,tvLongitude,tvReceived,
						tvDelivered,tvShipped, tvId;


	}

}
