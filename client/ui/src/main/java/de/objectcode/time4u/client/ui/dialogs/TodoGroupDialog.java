package de.objectcode.time4u.client.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.objectcode.time4u.client.store.api.IProjectRepository;
import de.objectcode.time4u.client.store.api.ITaskRepository;
import de.objectcode.time4u.client.store.api.RepositoryFactory;
import de.objectcode.time4u.client.ui.UIPlugin;
import de.objectcode.time4u.server.api.data.TodoGroup;
import de.objectcode.time4u.server.api.data.TodoState;

public class TodoGroupDialog extends Dialog
{
  private Text m_headerText;
  private Label m_stateLabel;
  private Text m_descriptionText;

  IProjectRepository m_projectRepository;
  ITaskRepository m_taskRepository;

  private final boolean m_create;
  private final TodoGroup m_todoGroup;

  public TodoGroupDialog(final IShellProvider shellProvider)
  {
    this(shellProvider, null);
  }

  public TodoGroupDialog(final IShellProvider shellProvider, final TodoGroup todoGroup)
  {
    super(shellProvider);

    setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
    m_projectRepository = RepositoryFactory.getRepository().getProjectRepository();
    m_taskRepository = RepositoryFactory.getRepository().getTaskRepository();

    if (todoGroup == null) {
      m_todoGroup = new TodoGroup();
      m_todoGroup.setHeader("");
      m_todoGroup.setDescription("");
      m_todoGroup.setState(TodoState.UNASSIGNED);
      m_create = true;
    } else {
      m_todoGroup = todoGroup;
      m_create = false;
    }
  }

  public TodoGroup getTodoGroup()
  {
    return m_todoGroup;
  }

  @Override
  protected void configureShell(final Shell newShell)
  {
    super.configureShell(newShell);

    if (m_create) {
      newShell.setText(UIPlugin.getDefault().getString("dialog.todoGroup.new.title"));
    } else {
      newShell.setText(UIPlugin.getDefault().getString("dialog.todoGroup.edit.title"));
    }
  }

  @Override
  protected Control createDialogArea(final Composite parent)
  {
    final Composite composite = (Composite) super.createDialogArea(parent);
    final Composite root = new Composite(composite, SWT.NONE);
    root.setLayout(new GridLayout(2, false));
    root.setLayoutData(new GridData(GridData.FILL_BOTH));

    final Label headerLabel = new Label(root, SWT.LEFT);
    headerLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    headerLabel.setText(UIPlugin.getDefault().getString("todo.header.label"));
    GridData gridData = new GridData(GridData.FILL_BOTH);
    m_headerText = new Text(root, SWT.BORDER);
    m_headerText.setLayoutData(gridData);
    m_headerText.setText(m_todoGroup.getHeader());

    final Label stateLabel = new Label(root, SWT.LEFT);
    stateLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    stateLabel.setText(UIPlugin.getDefault().getString("todo.state.label"));
    m_stateLabel = new Label(root, SWT.LEFT);
    gridData = new GridData(GridData.FILL_BOTH);
    m_stateLabel.setLayoutData(gridData);
    m_stateLabel.setText(UIPlugin.getDefault().getString("todo.state." + m_todoGroup.getState() + ".label"));

    final Label descriptionLabel = new Label(root, SWT.LEFT);
    descriptionLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    descriptionLabel.setText(UIPlugin.getDefault().getString("todo.description.label"));
    m_descriptionText = new Text(root, SWT.BORDER | SWT.MULTI);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.widthHint = convertWidthInCharsToPixels(60);
    gridData.heightHint = convertHeightInCharsToPixels(4);
    m_descriptionText.setLayoutData(gridData);
    m_descriptionText.setText(m_todoGroup.getDescription());
    m_descriptionText.setTextLimit(1000);

    return composite;
  }

  @Override
  protected Control createButtonBar(final Composite parent)
  {
    final Control control = super.createButtonBar(parent);

    enableOkButton();

    return control;
  }

  @Override
  protected void okPressed()
  {
    m_todoGroup.setHeader(m_headerText.getText());
    m_todoGroup.setDescription(m_descriptionText.getText());

    super.okPressed();
  }

  private void enableOkButton()
  {
    final Button button = getButton(IDialogConstants.OK_ID);

    if (button != null) {
      button.setEnabled(true);
    }
  }

}
