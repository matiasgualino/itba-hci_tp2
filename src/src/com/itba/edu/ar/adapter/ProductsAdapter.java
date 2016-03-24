package com.itba.edu.ar.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.itba.edu.ar.R;
import com.itba.edu.ar.model.Product;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoadingListener;

public class ProductsAdapter extends ArrayAdapter<Product> {

	private Activity activity;
	private List<Product> items;
	private Product objBean;
	private int row;
	private DisplayImageOptions options;
	ImageLoader imageLoader;

	public ProductsAdapter(Activity act, int resource, List<Product> arrayList) {
		super(act, resource, arrayList);
		this.activity = act;
		this.row = resource;
		this.items = arrayList;

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.image_empty)
				.showImageForEmptyUrl(R.drawable.image_empty).cacheInMemory()
				.cacheOnDisc().build();
		imageLoader = ImageLoader.getInstance();

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

		holder.tvDesc = (TextView) view.findViewById(R.id.tvdesc);
		holder.tvDate = (TextView) view.findViewById(R.id.tvdate);
		holder.tvBrand = (TextView) view.findViewById(R.id.tvbrand);
		holder.imgView = (ImageView) view.findViewById(R.id.image);
		holder.pbar = (ProgressBar) view.findViewById(R.id.pbar);

		if (holder.tvDesc != null && null != objBean.getName()
				&& objBean.getName().trim().length() > 0) {
			holder.tvDesc.setText(objBean.getName());
			
		}
		
		if (holder.tvBrand != null && null != objBean.getAttribute("Marca")
				&& objBean.getAttribute("Marca").trim().length() > 0) {
			holder.tvBrand.setText(objBean.getAttribute("Marca"));
			
		}
		
		
		if (holder.tvDate != null && null != objBean.getPrice()) {
			holder.tvDate.setText("$" + objBean.getPrice().toString());
			holder.tvDate.setTextColor(Color.parseColor(getContext().getString(R.color.red)));
			holder.tvDate.setTypeface(null, Typeface.BOLD);
		}
		if (holder.imgView != null) {
			if (null != objBean.getImageUrl()
					&& objBean.getImageUrl().get(0).trim().length() > 0) {
				final ProgressBar pbar = holder.pbar;

				imageLoader.init(ImageLoaderConfiguration
						.createDefault(activity));
				imageLoader.displayImage(objBean.getImageUrl().get(0), holder.imgView,
						options, new ImageLoadingListener() {
							@Override
							public void onLoadingComplete() {
								pbar.setVisibility(View.INVISIBLE);

							}

							@Override
							public void onLoadingFailed() {
								pbar.setVisibility(View.INVISIBLE);
							}

							@Override
							public void onLoadingStarted() {
								pbar.setVisibility(View.VISIBLE);

							}
						});

			} else {
				holder.imgView.setImageResource(R.drawable.ic_launcher);
			}
		}

		return view;
	}

	public class ViewHolder {

		public TextView tvBrand, tvDesc, tvDate;
		private ImageView imgView;
		private ProgressBar pbar;
		private RatingBar ratingBar;

	}

}