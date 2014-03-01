import java.awt.BorderLayout;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import Kalmia.Server.SwitchController;

import com.google.gdata.data.DateTime;


public class SwitchLibraryViewer extends JPanel {
	private static final long serialVersionUID = 1L;
	private LinkedList<SwitchController> switchControllers;
	private JTable table = new JTable();
	protected String[] columnNames = {"Title","Status","Climate Mode","Heat_SP","Cool_SP","Humidity","Last Update","Next Update"};
	private JPanelRepainter repainter = null;
	public SwitchLibraryViewer(){
		repainter = new JPanelRepainter(this);
		repainter.setRefresh(true);
		repainter.start();
	}
	public void addSwitches(LinkedList<SwitchController> s){
		switchControllers = s;
		table = new JTable(new SwitchTableModel());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//System.out.println("added switches to viewer");
		this.setLayout(new BorderLayout());
		add(new JScrollPane(table));
	}
	private class SwitchTableModel extends AbstractTableModel {
		static final long serialVersionUID = 1L;

		public int getColumnCount() {
	        return columnNames.length;
	    }

	    public int getRowCount() {
	        return switchControllers.size();
	    }

	    public String getColumnName(int col) {
	        return columnNames[col];
	    }

	    public Object getValueAt(int row, int col) {
	    	DateTime time = new DateTime(0);
	    	SwitchController tmp = switchControllers.get(row);
	    	switch (col){
	    	case 0:
	    		return tmp.getTitle();
	    	case 1:
	    		return tmp.getUiStatus();
	    	case 2:
	    		return tmp.getUiThermoMode();
	    	case 3:
	    		return tmp.getUiSP();
	    	case 4:
	    		return tmp.getUiSP();
	    	case 5:
	    		return tmp.getUiHumidity();
	    	case 6:
	    		time = tmp.getLastRun();
	    		if (time.compareTo(new DateTime(0)) == 0) return "never";
	    		time.setTzShift(0);
	    		return time.toUiString();
	    	case 7:
	    		time = tmp.getNextRun();
	    		if (time.compareTo(new DateTime(0)) == 0) return "never";
	    		time.setTzShift(0);
	    		return time.toUiString();
	    	default:
	    		return 0;
	    	}
	    }

	    public Class getColumnClass(int c) {
	    	switch (c){
	    	case 0:
	    		return String.class;
	    	case 1:	
	    		return String.class;
	    	case 2:	
	    		return String.class;
	    	case 3:	
	    		return String.class;
	    	default:
	    		return String.class;
	    	}
	    }

	    /*
	     * Don't need to implement this method unless your table's
	     * editable.
	     */
	    public boolean isCellEditable(int row, int col) {
	    	return false;
	    }

	    /*
	     * Don't need to implement this method unless your table's
	     * data can change.
	     */
	    /*public void setValueAt(Object value, int row, int col) {
	        data[row][col] = value;
	        fireTableCellUpdated(row, col);
	    }*/
	}
}

