package com.rdiachenko.jlv.ui.preferences.additional;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.rdiachenko.jlv.JlvActivator;
import com.rdiachenko.jlv.model.LogField;

public class LogListViewTableEditor extends FieldEditor {

//	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final Image CHECKED = JlvActivator.getImageDescriptor(
			"icons/checkboxChecked.gif").createImage();

	private static final Image UNCHECKED = JlvActivator.getImageDescriptor(
			"icons/checkboxUnchecked.gif").createImage();

	private static final String NAME_LABEL = "Name";
	private static final String WIDTH_LABEL = "Width";
	private static final String DISPLAY_LABEL = "Display";
	private static final String[] COLUMN_NAMES = { NAME_LABEL, WIDTH_LABEL, DISPLAY_LABEL };

	private static final int NAME_COLUMN_WIDTH = 120;
	private static final int WIDTH_COLUMN_WIDTH = 120;
	private static final int DISPLAY_COLUMN_WIDTH = 50;
	private static final int[] COLUMN_WIDTHS = { NAME_COLUMN_WIDTH, WIDTH_COLUMN_WIDTH, DISPLAY_COLUMN_WIDTH };

	private TableViewer tableViewer;

	private Composite buttonBox;
	private Button upButton;
	private Button downButton;

	private SelectionListener selectionListener;

	private LogListViewTableStructureModel[] tableModel;

	private TableModelLoader modelLoader;

	public LogListViewTableEditor(String name, Composite parent) {
		init(name, "");
		modelLoader = new TableModelLoader();
		createControl(parent);
	}

	@Override
	public void setFocus() {
		if (tableViewer != null) {
			tableViewer.getControl().setFocus();
		}
	}

	@Override
	public void adjustForNumColumns(int numColumns) {
		((GridData) tableViewer.getControl().getLayoutData()).horizontalSpan = numColumns;
	}

	@Override
	public int getNumberOfControls() {
		return 2; // Table and Button box are 2 controls
	}

	@Override
	public void doFillIntoGrid(Composite parent, int numColumns) {
		tableViewer = getTableViewerControl(parent);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableViewer.getControl().setLayoutData(gridData);

		buttonBox = getButtonBoxControl(parent);
		gridData = new GridData();
		gridData.verticalAlignment = GridData.BEGINNING;
		buttonBox.setLayoutData(gridData);
	}

	@Override
	public void doLoad() {
		doLoad(modelLoader.loadModel());
	}

	@Override
	public void doLoadDefault() {
		doLoad(modelLoader.loadDefaultModel());
	}

	@Override
	public void doStore() {
		if (tableViewer != null) {
			modelLoader.storeModel(tableModel);
		}
	}

	private void doLoad(LogListViewTableStructureModel[] model) {
		if (tableViewer != null) {
			for (int i = 0; i < model.length; i++) {
				tableModel[i] = model[i];
			}
			tableViewer.refresh();
		}
	}

	private SelectionListener getSelectionListener() {
		if (selectionListener == null) {
			createSelectionListener();
		}
		return selectionListener;
	}

	private TableViewer getTableViewerControl(Composite parent) {
		if (tableViewer == null) {
			tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION
					| SWT.HIDE_SELECTION);
			tableViewer.setUseHashlookup(true);
			Table table = tableViewer.getTable();
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.addSelectionListener(getSelectionListener());
			table.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					tableViewer = null;
				}
			});

			createTableColumns(tableViewer);
			tableModel = createTableModel();
			tableViewer.setContentProvider(new ArrayContentProvider());
			tableViewer.setInput(tableModel);
		} else {
			checkParent(tableViewer.getControl(), parent);
		}
		return tableViewer;
	}

	private LogListViewTableStructureModel[] createTableModel() {
		LogListViewTableStructureModel[] model = new LogListViewTableStructureModel[LogField.values().length];
		int index = 0;

		for (LogField logField : LogField.values()) {
			model[index] = new LogListViewTableStructureModel(logField.getName(), 100, true);
			index++;
		}
		return model;
	}

	private void createTableColumns(TableViewer tableViewer) {
		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.LEAD);
			viewerColumn.getColumn().setText(COLUMN_NAMES[i]);
			viewerColumn.getColumn().setWidth(COLUMN_WIDTHS[i]);

			switch (COLUMN_NAMES[i]) {
			case NAME_LABEL:
				viewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public String getText(final Object element) {
						LogListViewTableStructureModel column = (LogListViewTableStructureModel) element;
						return column.getName();
					}
				});
				break;
			case WIDTH_LABEL:
				viewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public String getText(final Object element) {
						LogListViewTableStructureModel column = (LogListViewTableStructureModel) element;
						return Integer.toString(column.getWidth());
					}
				});
				viewerColumn.setEditingSupport(new WidthCellEditor(tableViewer));
				break;
			case DISPLAY_LABEL:
				viewerColumn.setLabelProvider(new ColumnLabelProvider() {
					@Override
					public String getText(final Object element) {
						return null;
					}

					@Override
					public Image getImage(final Object element) {
						LogListViewTableStructureModel column = (LogListViewTableStructureModel) element;
						Image image = (column.isDisplay()) ? CHECKED : UNCHECKED;
						return image;
					}
				});
				viewerColumn.setEditingSupport(new DisplayCellEditor(tableViewer));
				break;
			default:
				throw new IllegalArgumentException("No column with such name: " + COLUMN_NAMES[i]
						+ ". Only [Name, Width, Display] are allowed.");
			}
		}
	}

	private Composite getButtonBoxControl(Composite parent) {
		if (buttonBox == null) {
			buttonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			buttonBox.setLayout(layout);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					upButton = null;
					downButton = null;
					buttonBox = null;
				}
			});
		} else {
			checkParent(buttonBox, parent);
		}
		selectionChanged();
		return buttonBox;
	}

	private void createButtons(Composite box) {
		upButton = createPushButton(box, "Up");
		downButton = createPushButton(box, "Down");
	}

	private Button createPushButton(Composite parent, String name) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(name);
		button.addSelectionListener(getSelectionListener());
		GridData gridData = new GridData(SWT.NONE);
		gridData.widthHint = 95;
		button.setLayoutData(gridData);
		return button;
	}

	private void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;

				if (widget == upButton) {
					upPressed();
				} else if (widget == downButton) {
					downPressed();
				} else if (widget == tableViewer.getControl()) {
					selectionChanged();
				}
			}
		};
	}

	private void selectionChanged() {
		int index = tableViewer.getTable().getSelectionIndex();
		int size = tableViewer.getTable().getItemCount();

		upButton.setEnabled(size > 1 && index > 0);
		downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
	}

	private void upPressed() {
		swap(true);
	}

	private void downPressed() {
		swap(false);
	}

	private void swap(boolean up) {
		int index = tableViewer.getTable().getSelectionIndex();
		int target = up ? index - 1 : index + 1;

		if (index >= 0) {
			LogListViewTableStructureModel currentModel = tableModel[index];
			tableModel[index] = tableModel[target];
			tableModel[target] = currentModel;
			currentModel = null;
			tableViewer.refresh();
			tableViewer.getTable().setSelection(target);
		}
		selectionChanged();
	}

	private static class WidthCellEditor extends EditingSupport {

		private TableViewer viewer;

		public WidthCellEditor(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			CellEditor cellEditor = new TextCellEditor(viewer.getTable());

			((Text) cellEditor.getControl()).addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(final VerifyEvent e) {
					e.doit = e.text.matches("[\\d]*");
				}
			});
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			LogListViewTableStructureModel model = (LogListViewTableStructureModel) element;
			return Integer.toString(model.getWidth());
		}

		@Override
		protected void setValue(Object element, Object value) {
			LogListViewTableStructureModel model = (LogListViewTableStructureModel) element;
			int width = Integer.valueOf((String) value);
			model.setWidth(width);
			viewer.update(element, null);
		}
	}

	private static class DisplayCellEditor extends EditingSupport {

		private final TableViewer viewer;

		public DisplayCellEditor(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			return new CheckboxCellEditor(viewer.getTable(), SWT.CHECK | SWT.CENTER);
		}

		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		@Override
		protected Object getValue(final Object element) {
			LogListViewTableStructureModel model = (LogListViewTableStructureModel) element;
			return model.isDisplay();
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			LogListViewTableStructureModel model = (LogListViewTableStructureModel) element;
			model.setDisplay((Boolean) value);
			viewer.update(element, null);
		}
	}

	private class TableModelLoader {

		private static final String COLUMN_SEPARATOR = ":";
		private static final String SEMICOLUMN_SEPARATOR = ";";

		public LogListViewTableStructureModel[] loadModel() {
			String prefs = getPreferenceStore().getString(getPreferenceName());
			return stringToModel(prefs);
		}

		public LogListViewTableStructureModel[] loadDefaultModel() {
			String property = getPreferenceStore().getDefaultString(getPreferenceName());
			return stringToModel(property);
		}

		public void storeModel(LogListViewTableStructureModel[] model) {
			String prefs = modelToString(model);
			getPreferenceStore().setValue(getPreferenceName(), prefs);
		}

		private LogListViewTableStructureModel[] stringToModel(String prefs) {
			String[] modelItems = prefs.split(SEMICOLUMN_SEPARATOR);
			LogListViewTableStructureModel[] model = new LogListViewTableStructureModel[modelItems.length];

			for (int i = 0; i < model.length; i++) {
				String[] modelItem = modelItems[i].split(COLUMN_SEPARATOR);
				String name = modelItem[0];
				int width = Integer.valueOf(modelItem[1]);
				boolean display = Boolean.valueOf(modelItem[2]);
				model[i] = new LogListViewTableStructureModel(name, width, display);
			}
			return model;
		}

		private String modelToString(LogListViewTableStructureModel[] model) {
			StringBuilder builder = new StringBuilder();

			for (LogListViewTableStructureModel modelItem : model) {
				builder.append(modelItem.getName()).append(COLUMN_SEPARATOR)
						.append(modelItem.getWidth()).append(COLUMN_SEPARATOR)
						.append(modelItem.isDisplay()).append(SEMICOLUMN_SEPARATOR);
			}
			return builder.toString();
		}
	}
}