import java.awt.BorderLayout;
import java.time.ZonedDateTime;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import Kalmia.Server.CalendarListener;


public class CalenderLibraryViewer extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedList<CalendarListener> listeners;
	private JTable table = new JTable();
	private String[] columnNames = {"Title","Next Event Title","Next Event Start","Last Update","Next Update"};
	private JPanelRepainter repainter = null;
	public CalenderLibraryViewer(){
		repainter = new JPanelRepainter(this);
		repainter.setRefresh(true);
		repainter.start();
	}
	public void addListeners(LinkedList<CalendarListener> c){
		listeners = c;
		table = new JTable(new CalendarTableModel());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//System.out.println("added calendars to viewer");
		this.setLayout(new BorderLayout());
		add(new JScrollPane(table));
	}
	private class CalendarTableModel extends AbstractTableModel {
		static final long serialVersionUID = 1L;

		public int getColumnCount() {
	        return columnNames.length;
	    }

	    public int getRowCount() {
	        return listeners.size();
	    }

	    public String getColumnName(int col) {
	        return columnNames[col];
	    }

	    public Object getValueAt(int row, int col) {
	    	ZonedDateTime time;
	    	CalendarListener tmp = listeners.get(row);
	    	switch (col){
	    	case 0:
	    		return tmp.getTitle();
	    	case 1:
	    		return tmp.getNextEventTitle();
	    	case 2:	
	    		time = tmp.getNextEventST();
	    		if(time!=null) return time.toLocalDate().toString()+",  "+time.getHour()+":"+String.format("%02d", time.getMinute());
	    		else return "";
	    	case 3:	
	    		time = tmp.getLastRun();
	    		if(time!=null) return time.toLocalDate().toString()+",  "+time.getHour()+":"+String.format("%02d", time.getMinute());
	    		else return "";
	    	case 4:	
	    		time = tmp.getNextRun();
	    		if(time!=null) return time.toLocalDate().toString()+",  "+time.getHour()+":"+String.format("%02d", time.getMinute());
	    		else return "";
	    	default:
	    		return 0;
	    	}
	    }

		public Class<String> getColumnClass(int c) {
	    	return String.class;
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
