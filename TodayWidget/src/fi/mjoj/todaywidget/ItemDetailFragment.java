package fi.mjoj.todaywidget;

import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link ItemListActivity} in two-pane mode (on tablets) or a
 * {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private String mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Set<String> keys = getArguments().keySet();
		
		
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = getArguments().getString(ARG_ITEM_ID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_detail,
				container, false);

		CalendarContentResolver ccr = CalendarContentResolver.getInstance();
		ccr.updateCalendarContentCache(getActivity().getContentResolver());
		
		StringBuffer sb = new StringBuffer();
		
		for (CalendarEventEntry event : ccr.getEventsForToday(Integer.parseInt(mItem)).values()) {
			sb.append(event.getStartDate() + "; " + event.getEndDate() + "; " + event.getTitle());
			sb.append(" ");
		}
		
		((TextView) rootView.findViewById(R.id.item_detail)).setText(sb.toString());

		return rootView;
	}
}
