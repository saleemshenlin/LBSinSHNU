package com.shnu.lbsshnu;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;

/**
 * ��SectionedAdapter<br>
 * ���ڌ����adapter������һ��adapter��<br>
 * �ܹ�ʵ��ResultActivity�е�list�󶨶��adapter.<br>
 */
abstract public class SectionedAdapter extends BaseAdapter {
	abstract protected View getHeaderView(String caption, String distance,
			int index, View convertView, ViewGroup parent);

	/**
	 * ����һ��List<Section> ������Adapter
	 */
	private List<Section> sections = new ArrayList<Section>();

	private static int TYPE_SECTION_HEADER = 0;

	public SectionedAdapter() {
		super();
	}

	public void addSection(String caption, String distance, Adapter adapter) {
		sections.add(new Section(caption, distance, adapter));
	}

	public Object getItem(int position) {
		for (Section section : this.sections) {
			if (position == 0) {
				return (section);
			}

			int size = section.adapter.getCount() + 1;
			if (position < size) {
				return (section.adapter.getItem(position - 1));
			}
			position -= size;
		}
		return (null);
	}

	/**
	 * ���ڼ���һ���ж���item<br>
	 * ����ÿ��adapter��,��ȡ������,�ڼ�������
	 */
	public int getCount() {
		int total = 0;

		for (Section section : this.sections) {
			total += section.adapter.getCount() + 1;
		}
		return (total);
	}

	public int getViewTypeCount() {
		int total = 1; // one for the header, plus those from sections

		for (Section section : this.sections) {
			total += section.adapter.getViewTypeCount();
		}
		return (total);
	}

	public int getItemViewType(int position) {
		int typeOffset = TYPE_SECTION_HEADER + 1; // start counting from here
		for (Section section : this.sections) {
			if (position == 0) {
				return (TYPE_SECTION_HEADER);
			}
			int size = section.adapter.getCount() + 1;
			if (position < size) {
				return (typeOffset + section.adapter
						.getItemViewType(position - 1));
			}
			position -= size;
			typeOffset += section.adapter.getViewTypeCount();
		}
		return (-1);
	}

	public boolean areAllItemsSelectable() {
		return (false);
	}

	public boolean isEnabled(int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionIndex = 0;
		for (Section section : this.sections) {
			if (position == 0) {
				return (getHeaderView(section.caption, section.distance,
						sectionIndex, convertView, parent));
			}
			int size = section.adapter.getCount() + 1;
			if (position < size) {
				return (section.adapter.getView(position - 1, convertView,
						parent));
			}
			position -= size;
			sectionIndex++;
		}
		return (null);
	}

	@Override
	public long getItemId(int position) {
		return (position);
	}

	class Section {
		String caption;
		String distance;
		Adapter adapter;

		Section(String caption, String distance, Adapter adapter) {
			this.caption = caption;
			this.adapter = adapter;
			this.distance = distance;
		}
	}
}
