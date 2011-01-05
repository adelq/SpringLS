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
import com.springrts.tasserver.commands.AbstractCommandProcessor;
import com.springrts.tasserver.commands.CommandProcessingException;
import com.springrts.tasserver.commands.SupportedCommand;
import java.util.List;

/**
 * Allows an administrator to set/unset the bot mode on an account.
 * @author hoijui
 */
@SupportedCommand("SETBOTMODE")
public class SetBotModeCommandProcessor extends AbstractCommandProcessor {

	public SetBotModeCommandProcessor() {
		super(2, 2, Account.Access.ADMIN);
	}

	@Override
	public boolean process(Client client, List<String> args)
			throws CommandProcessingException
	{
		boolean checksOk = super.process(client, args);
		if (!checksOk) {
			return false;
		}

		int mode;
		try {
			mode = Integer.parseInt(args.get(1));
		} catch (NumberFormatException e) {
			client.sendLine("SERVERMSG Invalid 'mode' parameter (must be 0 or 1)!");
			return false;
		}
		if ((mode != 0) && (mode != 1)) {
			client.sendLine("SERVERMSG Invalid 'mode' parameter (must be 0 or 1)!");
			return false;
		}

		String userName = args.get(0);

		Account acc = getContext().getAccountsService().getAccount(userName);
		if (acc == null) {
			client.sendLine(new StringBuilder("SERVERMSG User <").append(userName).append("> not found!").toString());
			return false;
		}

		final boolean wasBot = acc.isBot();
		acc.setBot((mode == 0) ? false : true);
		final boolean mergeOk = getContext().getAccountsService().mergeAccountChanges(acc, acc.getName());
		if (!mergeOk) {
			acc.setBot(wasBot);
			client.sendLine("SERVERMSG SETBOTMODE failed: Failed saving to persistent storage.");
			return false;
		}

		client.sendLine(new StringBuilder("SERVERMSG Bot mode set to ")
				.append(mode).append(" for user <")
				.append(acc.getName()).append(">").toString());

		return true;
	}
}
