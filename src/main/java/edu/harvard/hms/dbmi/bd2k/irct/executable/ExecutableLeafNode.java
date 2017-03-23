/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package edu.harvard.hms.dbmi.bd2k.irct.executable;

import edu.harvard.hms.dbmi.bd2k.irct.action.Action;
import edu.harvard.hms.dbmi.bd2k.irct.event.IRCTEventListener;
import edu.harvard.hms.dbmi.bd2k.irct.exception.ResourceInterfaceException;
import edu.harvard.hms.dbmi.bd2k.irct.model.result.Job;
import edu.harvard.hms.dbmi.bd2k.irct.model.security.SecureSession;
import edu.harvard.hms.dbmi.bd2k.irct.util.Utilities;

/**
 * A leaf node in an execution tree that can be executed. It does not have any
 * children
 * 
 * @author Jeremy R. Easton-Marks
 *
 */
public class ExecutableLeafNode implements Executable {

	private SecureSession session;
	private Action action;
	private ExecutableStatus state;
	
	private IRCTEventListener irctEventListener;
	private Executable parent;

	@Override
	public void setup(SecureSession secureSession) {
		this.session = secureSession;
		this.state = ExecutableStatus.CREATED;
		
		if(this.irctEventListener == null) {
			this.irctEventListener = Utilities.getIRCTEventListener();
		}
	}

	@Override
	public void run() throws ResourceInterfaceException {
		irctEventListener.beforeAction(session, action);
		
		this.state = ExecutableStatus.RUNNING;
		this.action.run(this.session);
		this.state = ExecutableStatus.COMPLETED;
		
		irctEventListener.afterAction(session, action);
	}

	@Override
	public ExecutableStatus getStatus() {
		return this.state;
	}

	@Override
	public Job getResults() throws ResourceInterfaceException {
		return this.action.getResults(this.session);
	}

	@Override
	public Action getAction() {
		return action;
	}

	/**
	 * Sets the action that is to be executed
	 * 
	 * @param action
	 *            Action
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public Executable getParent() {
		return this.parent;
	}

	@Override
	public void setParent(Executable parent) {
		this.parent = parent;
		
	}
}
