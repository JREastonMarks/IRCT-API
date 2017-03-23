/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package edu.harvard.hms.dbmi.bd2k.irct.event.result;

import edu.harvard.hms.dbmi.bd2k.irct.event.IRCTEvent;
import edu.harvard.hms.dbmi.bd2k.irct.model.result.Job;

/**
 * An event listener that is run before a result is retrieved
 * 
 * @author Jeremy R. Easton-Marks
 *
 */
public interface BeforeSaveResult extends IRCTEvent {

	void fire(Job result);

}
