package org.eclipse.birt.core.ui.frameworks.taskwizard.interfaces;

import java.util.Locale;

import org.eclipse.birt.core.ui.frameworks.taskwizard.WizardBase;
import org.eclipse.swt.widgets.Composite;

public interface ITask
{
    public Composite getUI(Composite parent);

    public String getDisplayLabel(Locale locale);

    /**
     * Called just before the UI for this Task is shown. Intended for the UI to prepare itself by processing 
     * the context passed in. The context will depend on the wizard.
     * 
     * @param context The context in which the Subtask UI will be shown. Its content depend on individual wizards.
     */
    public void setContext(IWizardContext context);

    /**
     * Called just before the UI for the task is disposed, it will be used by the wizard. The context returned 
     * should contain the complete updated context for the wizard. This context should be useable as is and should 
     * not require any additional processing before use.
     *  
     * @return complete context for the wizard with all updates that result from operations performed as part of this
     * task.
     */
    public IWizardContext getContext();
    
    /**
     * Called upon instantiation to allow a task to interact with the containing wizard. This instance should be used
     * to perform operations like enabling or disabling other tasks in the current invocation as well as to display or
     * hide linked popup windows.
     *  
     * @param wizard instance of WizardBase containing this task instance.
     */
    public void setUIProvider(WizardBase wizard);
    
    /**
     * Called just before the UI for this Task is shown...in case the task is being shown in response to a request to
     * fix an error. The argument is supposed to be an object array that provides additional information as to the specific
     * error condition in the current context.
     * 
     * @param errorHints Object array providing additional (task-specific) information provided by the source of the task
     * redirection. It is expected to contain information that the target task can use to better guide the user to fix any
     * error conditions.
     */
    public void setErrorHints(Object[] errorHints);

    /**
     * Called by the container wizard if it follows the 'pull' mechanism for error discovery. A task is expected to 
     * provide a collection of error messages indicating the error conditions currently existing in the wizard context.
     * If a wizard does not follow the 'pull' mechanism, the task needs to explicitly 'push' the error list to the wizard
     * using the 'displayError()' method.
     * 
     * @return Array of strings representing any error conditions existing in the context
     */
    public String[] getErrors();
}