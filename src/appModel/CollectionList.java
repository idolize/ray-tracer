package appModel;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;

import view.CollectionToolbar;

@SuppressWarnings("serial")
public class CollectionList<T> extends JList {
	private CollectionListModel model;
	private ArrayList<T> list;
	private CollectionToolbar toolbar;

	public CollectionList(ArrayList<T> list, CollectionToolbar toolbar) {
		this.list = list;
		this.toolbar = toolbar;
		model = new CollectionListModel();
		this.setModel(model);
		this.setSelectionModel(new CollectionSelectionModel());
	}

	public CollectionListModel getModel() {
		return model;
	}

	public ArrayList<T> getList() {
		return list;
	}

	public CollectionToolbar getToolbar() {
		return toolbar;
	}

	class CollectionListModel extends AbstractListModel implements Observer {
		public T getElementAt(int index) {
			return list.get(index);
		}

		public int getSize() {
			return list.size();
		}

		public void update(Observable o, Object arg) {
			fireContentsChanged(this, 0, 0);
		}
	}

	class CollectionSelectionModel extends DefaultListSelectionModel {
		CollectionSelectionModel() {
			this.setSelectionMode(SINGLE_SELECTION);
		}
		
		public void setSelectionInterval(int index0, int index1) {
			if (isSelectedIndex(index0)) {
				clearSelection();
				return;
			}
			ensureIndexIsVisible(index0);
			toolbar.enableRemoveAndEdit(true);
			super.setSelectionInterval(index0, index1);
		}
		
		public void clearSelection() {
			toolbar.enableRemoveAndEdit(false);
			super.clearSelection();
		}
	}
}
