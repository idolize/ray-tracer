package view;

import java.awt.Component;
import java.awt.event.*;
import javax.swing.*;


@SuppressWarnings("serial")
public abstract class CollectionToolbar extends JToolBar implements ActionListener {
	protected Component parent;
	private JButton editBtn, addBtn, removeBtn;
	private static final String add = "a";
	private static final String remove = "r";
	private static final String edit = "e";

	public CollectionToolbar(Component parent) {
		this.parent = parent;
		setFloatable(false);

		// edit button
		editBtn = new JButton("Edit");
		editBtn.setActionCommand(edit);
		editBtn.setToolTipText("Edit selected");
		editBtn.addActionListener(this);
		editBtn.setEnabled(false);
		add(editBtn);
		
		// remove button
		removeBtn = new JButton("Remove");
		removeBtn.setActionCommand(remove);
		removeBtn.setToolTipText("Remove selected");
		removeBtn.addActionListener(this);
		removeBtn.setEnabled(false);
		add(removeBtn);
		
		// add button
		addBtn = new JButton("Add");
		addBtn.setActionCommand(add);
		addBtn.setToolTipText("Add new");
		addBtn.addActionListener(this);
		add(addBtn);
	}
	
	public CollectionToolbar() {
		this(null);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (add.equals(cmd)) {
			addBtnClicked();			
		}
		else if (remove.equals(cmd)) {
			removeBtnClicked();
		}
		else {
			editBtnClicked();
		}
	}
	
	public void enableRemoveAndEdit(boolean enabled) {
		removeBtn.setEnabled(enabled);
		editBtn.setEnabled(enabled);
	}
	
	abstract void addBtnClicked();
	
	abstract void removeBtnClicked();
	
	abstract void editBtnClicked();
}