/*
	Copyright (c) 2010 Robin Vobruba <robin.vobruba@derisk.ch>

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.springrts.tasserver.commands.impl;


import com.springrts.tasserver.Account;
import com.springrts.tasserver.Client;
import com.springrts.tasserver.Misc;
import com.springrts.tasserver.commands.AbstractCommandProcessor;
import com.springrts.tasserver.commands.CommandProcessingException;
import com.springrts.tasserver.commands.SupportedCommand;
import java.util.List;

/**
 * @author hoijui
 */
@SupportedCommand("FINDIP")
public class FindIpCommandProcessor extends AbstractCommandProcessor {

	public FindIpCommandProcessor() {
		super(1, 1, Account.Access.PRIVILEGED);
	}

	@Override
	public boolean process(Client client, List<String> args)
			throws CommandProcessingException
	{
		boolean checksOk = super.process(client, args);
		if (!checksOk) {
			return false;
		}

		boolean found = false;
		String ip = args.get(0);
		String[] sp1 = ip.split("\\.");
		if (sp1.length != 4) {
			client.sendLine(new StringBuilder("SERVERMSG Invalid IP address/range: ").append(ip).toString());
			return false;
		}

		for (int i = 0; i < getContext().getClients().getClientsSize(); i++) {
			if (!Misc.isSameIP(sp1, getContext().getClients().getClient(i).getIp())) {
				continue;
			}

			found = true;
			client.sendLine(new StringBuilder("SERVERMSG ")
					.append(ip).append(" is bound to: ")
					.append(getContext().getClients().getClient(i).getAccount().getName()).toString());
		}

		// now let's check if this ip matches any recently used ip:
		Account lastAct = getContext().getAccountsService().findAccountByLastIP(ip);
		if (lastAct != null && getContext().getClients().getClient(lastAct.getName()) == null) { // user is offline
			found = true;
			client.sendLine(new StringBuilder("SERVERMSG ")
					.append(ip).append(" was recently bound to: ")
					.append(lastAct.getName()).append(" (offline)").toString());
		}

		if (!found) {
			//*** perhaps add an explanation like "(note that server only keeps track of last used ip addresses)" ?
			client.sendLine(new StringBuilder("SERVERMSG No client is/was recently using IP: ").append(ip).toString());
		}

		return true;
	}
}
