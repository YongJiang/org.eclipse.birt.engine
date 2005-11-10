/***********************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/
package org.eclipse.birt.core.ui.frameworks.errordisplay;

import org.eclipse.birt.core.ui.frameworks.taskwizard.composites.MessageComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @author Actuate Corporation
 *
 */
public class ErrorDialog implements SelectionListener
{
    // CONSTANTS
    public static final String OPTION_ACCEPT = "ACCEPT";    //$NON-NLS-1$
    public static final String OPTION_CANCEL = "CANCEL";    //$NON-NLS-1$
    public static final int MAX_TRACE_DEPTH = 2;
    public static final int DEFAULT_WIDTH = 450;
    public static final int DEFAULT_HEIGHT = 200;
    public static final int MAX_HEIGHT = 400;
    
    // UI COMPONENTS
    private transient Display display = Display.getDefault();
    private transient Shell shell = new Shell(display, SWT.DIALOG_TRIM);
    private transient Label lblImage = null;
    private transient Composite cmpContainer = null;
    private transient Composite cmpDetails = null;
    private transient StackLayout slDetails = null;
    private transient Group grpDetails = null;
    private transient Text txtDetails = null;
    private transient Composite cmpDummy = null;
    private transient MessageComposite mcSheetHeading = null;
    private transient Button btnOK = null;
    private transient Button btnDetails = null;
    private transient Label lblProblems = null;
    private transient Button btnCancel = null;
    private transient Group grpProblems = null;
    
    // COMMON DATA FIELDS
    private transient String sMessage = null;
    private transient String sSelection = OPTION_CANCEL;
    private transient boolean bError = true;
    
    // ERROR DIALOG DATA FIELDS
    private transient String sErrors = null;
    private transient String sFixes = null;
    
    // EXCEPTION DIALOG DATA FIELDS
    private transient String sExceptionMessage = null;
    private transient String sTrace = null;
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        ErrorDialog err = new ErrorDialog("Test Error Dialog", "An error has occurred!", new String[] {"No data has been associated with this object."}, new String[] {"Each object needs to have data associated with it. This data can be associated in the 'X' tab of the dialg. This data can be associated in the 'X' tab of the dialg. This data can be associated in the 'X' tab of the dialg. This data can be associated in the 'X' tab of the dialg."});
//        Throwable t = new RuntimeException("This is a test exception!");
//        ErrorDialog err = new ErrorDialog("Test Exception Dialog", "An exception has occurred!", t);
        System.out.println("Dialog Selection - "+err.getOption());
    }

    private void init(String sTitle)
    {
        display = Display.getDefault();
		if ( PlatformUI.isWorkbenchRunning( ) )
		{
			shell = new Shell( PlatformUI.getWorkbench( )
					.getDisplay( )
					.getActiveShell( ), SWT.DIALOG_TRIM
					| SWT.RESIZE | SWT.APPLICATION_MODAL );
		}
		else
		{
			shell = new Shell( display, SWT.DIALOG_TRIM
					| SWT.RESIZE | SWT.APPLICATION_MODAL );
		}
        shell.setText(sTitle);
        shell.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        shell.setLayout(new FillLayout());
        // CENTER THE DIALOG ON SCREEN
        shell.setLocation((display.getClientArea().width / 2) - (shell.getSize().x / 2), (display.getClientArea().height / 2) - (shell.getSize().y / 2));
        placeComponents();
        shell.setDefaultButton(btnOK);
        shell.open();
        while(!shell.isDisposed())
        {
            if(!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    public ErrorDialog(String sTitle, String sMessage, String[] sErrors, String[] sFixes)
    {
        this.sMessage = sMessage;
        this.sErrors = getOrganizedErrors(sErrors);
        this.sFixes = getOrganizedFixes(sFixes);
        this.bError = true;
        init(sTitle);
    }
    
    public ErrorDialog(String sTitle, String sMessage, Throwable t)
    {
        this.sMessage = sMessage;
        this.sExceptionMessage = t.getLocalizedMessage();
        this.sTrace = getOrganizedTrace(t);
        this.bError = false;
        init(sTitle);
    }
    
    private void placeComponents()
    {
        // CONTAINER LAYOUT
        GridLayout glDialog = new GridLayout();
        glDialog.numColumns = 3;
        glDialog.marginWidth = 6;
        glDialog.marginHeight = 6;
        glDialog.horizontalSpacing = 5;
        glDialog.verticalSpacing = 2;
        
        // CONTAINER
        cmpContainer = new Composite(shell, SWT.NONE);
        cmpContainer.setLayout(glDialog);

        // MESSAGE LABEL
        mcSheetHeading = new MessageComposite( cmpContainer,
            "", sMessage, "", true ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        GridData gdMSGHeading = new GridData( GridData.FILL_HORIZONTAL );
        gdMSGHeading.heightHint = 22;
        gdMSGHeading.horizontalSpan = 3;
        mcSheetHeading.setLayoutData( gdMSGHeading );
        mcSheetHeading.setBackground( display.getSystemColor(SWT.COLOR_WHITE) );
    
        // ICON
        lblImage = new Label(cmpContainer, SWT.NONE);
        GridData gdLblImage = new GridData();
        lblImage.setImage(display.getSystemImage(SWT.ICON_ERROR));
        lblImage.setLayoutData(gdLblImage);
        
        // PROBLEMS LABEL
        grpProblems = new Group(cmpContainer, SWT.NONE);
        GridData gdGrpProblems = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        gdGrpProblems.horizontalSpan = 2;
        gdGrpProblems.heightHint = 40;
        grpProblems.setLayoutData(gdGrpProblems);
        FillLayout flProblems = new FillLayout();
        flProblems.marginWidth = 2;
        flProblems.marginHeight = 2;
        grpProblems.setLayout(flProblems);
        
        lblProblems = new Label(grpProblems, SWT.WRAP);
        
        // DETAILS BUTTON
        Label lblButtonSpacer = new Label(cmpContainer, SWT.NONE);
        
        btnDetails = new Button(cmpContainer, SWT.TOGGLE);
        GridData gdBtnDetails = new GridData();
        gdBtnDetails.horizontalSpan = 2;
        btnDetails.setLayoutData(gdBtnDetails);
        btnDetails.addSelectionListener(this);
        
        // SOLUTIONS LABEL
        Label lblDetailSpacer = new Label(cmpContainer, SWT.NONE);

        slDetails = new StackLayout();
        
        cmpDetails = new Composite(cmpContainer, SWT.NONE);
        GridData gdCmpDetails = new GridData(GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
        gdCmpDetails.horizontalSpan = 2;
        cmpDetails.setLayoutData(gdCmpDetails);
        cmpDetails.setLayout(slDetails);

        cmpDummy = new Composite(cmpDetails, SWT.NONE);
        
        grpDetails = new Group(cmpDetails, SWT.NONE);
        FillLayout flSolutions = new FillLayout();
        flSolutions.marginWidth = 5;
        flSolutions.marginHeight = 5;
        grpDetails.setLayout(flSolutions);
        
        txtDetails = new Text(grpDetails, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        
        // BUTTON PANEL LAYOUT
        GridLayout glButtons = new GridLayout();
        glButtons.numColumns = 2;
        glButtons.horizontalSpacing = 5;
        glButtons.marginHeight = 5;
        glButtons.marginWidth = 5;
        
        // BUTTON PANEL
        Composite cmpButtons = new Composite(cmpContainer, SWT.NONE);
        GridData gdCmpButtons = new GridData(GridData.FILL_HORIZONTAL);
        gdCmpButtons.horizontalSpan = 3;
        cmpButtons.setLayoutData(gdCmpButtons);
        cmpButtons.setLayout(glButtons);
        
        // ACCEPT BUTTON
        btnOK = new Button(cmpButtons, SWT.NONE);
        GridData gdBtnOK = null;
        if(bError)
        {
            gdBtnOK = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
        }
        else
        {
            gdBtnOK = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
        }
        btnOK.setLayoutData(gdBtnOK);
        btnOK.addSelectionListener(this);
        
        // CANCEL BUTTON
        if(bError)
        {
            btnCancel = new Button(cmpButtons, SWT.NONE);
            GridData gdBtnCancel = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
            btnCancel.setLayoutData(gdBtnCancel);
            btnCancel.addSelectionListener(this);
        }

        if(bError)
        {
            grpProblems.setText("Error List:");
            lblProblems.setText(sErrors);
            btnDetails.setText("Show Details...");
            grpDetails.setText("Suggested Fixes:");
            btnOK.setText("   Fix It   ");
            //TODO unsupported now
            btnOK.setEnabled(false);
            btnCancel.setText("Proceed Without Fixing");
            if ( sFixes == null || sFixes.length( ) == 0 )
			{
            	btnDetails.setEnabled( false );
			}
			else
			{
				btnDetails.setEnabled( true );
				txtDetails.setText( sFixes );
			}
        }
        else
        {
            grpProblems.setText("Exception:");
            lblProblems.setText(sExceptionMessage);
            btnDetails.setText("Show Trace...");
            grpDetails.setText("Stack Trace:");
            btnOK.setText("     OK     ");
            if ( sTrace == null || sTrace.length( ) == 0 )
			{
            	btnDetails.setEnabled( false );
			}
			else
			{
				btnDetails.setEnabled( true );
				txtDetails.setText( sTrace );
			}
        }

        slDetails.topControl = cmpDummy;
        shell.setSize(shell.getSize().x, DEFAULT_HEIGHT);
        shell.layout();
    }
        
    public String getOption()
    {
        return sSelection;
    }

    /**
     * @param errors
     * @return
     */
    private String getOrganizedErrors(String[] errors)
    {
        StringBuffer sbErrors = new StringBuffer("");   //$NON-NLS-1$
        for(int i = 0; i < errors.length; i++)
        {
            if(i > 0)
            {
                sbErrors.append("\n");  //$NON-NLS-1$
            }
            sbErrors.append(errors[i]);
        }
        return sbErrors.toString();
    }

    /**
     * @param fixes
     * @return
     */
    private String getOrganizedFixes(String[] fixes)
    {
        StringBuffer sbFixes = new StringBuffer("");    //$NON-NLS-1$
        for(int i = 0; i < fixes.length; i++)
        {
            if(i > 0)
            {
                sbFixes.append("\n");   //$NON-NLS-1$
            }
            sbFixes.append(fixes[i]);
        }
        return sbFixes.toString();
    }

    /**
     * @param t
     * @return
     */
    private String getOrganizedTrace(Throwable t)
    {
        StringBuffer sbTrace = new StringBuffer("at:\n");    //$NON-NLS-1$
        for(int d = 0; d < MAX_TRACE_DEPTH; d++)
        {
            if(d > 0)
            {
            	if ( t.getCause( ) != null )
				{
					t = t.getCause( );
				}
                sbTrace.append("\n\nCaused By:"+t.getLocalizedMessage()+"\n");
            }
            StackTraceElement[] se = t.getStackTrace();
            for(int i = 0; i < se.length; i++)
            {
                if(i > 0)
                {
                    sbTrace.append("\n");   //$NON-NLS-1$
                }
                sbTrace.append(se[i].toString());
            }
        }
        return sbTrace.toString();
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected(SelectionEvent e) { }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent e)
    {
        if(e.getSource().equals(btnOK))
        {
            this.sSelection = OPTION_ACCEPT;
            shell.dispose();
        }
        else if(e.getSource().equals(btnCancel))
        {
            this.sSelection = OPTION_CANCEL;
            shell.dispose();
        }
        else if(e.getSource().equals(btnDetails))
        {
            toggleDetails(btnDetails.getSelection());
        }
    }

    /**
     * 
     */
    private void toggleDetails(boolean bVisible)
    {
        if(bVisible)
        {
            slDetails.topControl = grpDetails;
            shell.setSize(shell.getSize().x, MAX_HEIGHT);
        }
        else
        {
            slDetails.topControl = cmpDummy;
            shell.setSize(shell.getSize().x, DEFAULT_HEIGHT);
        }
        shell.layout();
    }
}
