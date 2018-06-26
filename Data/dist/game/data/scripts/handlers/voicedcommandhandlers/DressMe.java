package handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance.VisualArmors;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CharInfo;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

public class DressMe implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"dressme",
		"dressmetarget",
		"dressmestate",
		"dressmeremovesaveddata"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (command.equalsIgnoreCase("dressmeremovesaveddata"))
		{
			activeChar.removeVisualArmors();
			
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(activeChar.getHtmlPrefix(), "data/html/DressMe/Menu.htm");
			html.replace("%items%", showItems(activeChar));
			html.replace("%visualArmorState%", "You are currently " + (activeChar.isUsingVisualArmors() ? "enabled" : "disabled") + " your Visual Armors! <a action=\"bypass -h .dressmestate\">Turn " + (activeChar.isUsingVisualArmors() ? "off" : "on") + " Visual Armors!</a>");
			activeChar.sendPacket(html);
		}
		else if (command.equalsIgnoreCase("dressme"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(activeChar.getHtmlPrefix(), "data/html/DressMe/Menu.htm");
			html.replace("%items%", showItems(activeChar));
			html.replace("%visualArmorState%", "You are currently " + (activeChar.isUsingVisualArmors() ? "enabled" : "disabled") + " your Visual Armors! <a action=\"bypass -h .dressmestate\">Turn " + (activeChar.isUsingVisualArmors() ? "off" : "on") + " Visual Armors!</a>");
			activeChar.sendPacket(html);
		}
		else if (command.equalsIgnoreCase("dressmetarget"))
		{
			if (!(activeChar.getTarget() instanceof L2PcInstance) || (activeChar.getTarget() == activeChar))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				useVoicedCommand("dressme", activeChar, "");
				return false;
			}
			L2PcInstance target = ((L2PcInstance) activeChar.getTarget());
			if (target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) != null)
			{
				switch (target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).getWeaponItem().getItemType())
				{
					case SWORD:
					case BLUNT:
					case DAGGER:
					case ETC:
					case FIST:
					case RAPIER:
						activeChar.setVisualArmor(VisualArmors.Sword, target.getVisualArmor(VisualArmors.RHand, false).getItemId());
						break;
					case BOW:
					case CROSSBOW:
						activeChar.setVisualArmor(VisualArmors.Bow, target.getVisualArmor(VisualArmors.RHand, false).getItemId());
						break;
					case POLE:
						activeChar.setVisualArmor(VisualArmors.Pole, target.getVisualArmor(VisualArmors.RHand, false).getItemId());
						break;
					case DUAL:
					case DUALFIST:
					case DUALDAGGER:
						activeChar.setVisualArmor(VisualArmors.Dual, target.getVisualArmor(VisualArmors.RHand, false).getItemId());
						break;
					case ANCIENTSWORD:
					case BIGBLUNT:
					case BIGSWORD:
						activeChar.setVisualArmor(VisualArmors.BigSword, target.getVisualArmor(VisualArmors.RHand, false).getItemId());
						break;
				}
			}
			if ((target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) != null) && target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND).isArmor())
			{
				activeChar.setVisualArmor(VisualArmors.LHand, target.getVisualArmor(VisualArmors.LHand, false).getItemId());
			}
			if (target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST) != null)
			{
				activeChar.setVisualArmor(VisualArmors.Armor, target.getVisualArmor(VisualArmors.Armor, false).getItemId());
			}
			if ((target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) != null) && (target.getVisualArmor(VisualArmors.Legs, false) != null))
			{
				activeChar.setVisualArmor(VisualArmors.Legs, target.getVisualArmor(VisualArmors.Legs, false).getItemId());
			}
			if (target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) != null)
			{
				activeChar.setVisualArmor(VisualArmors.Feet, target.getVisualArmor(VisualArmors.Feet, false).getItemId());
			}
			if (target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) != null)
			{
				activeChar.setVisualArmor(VisualArmors.Gloves, target.getVisualArmor(VisualArmors.Gloves, false).getItemId());
			}
			if (target.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CLOAK) != null)
			{
				activeChar.setVisualArmor(VisualArmors.Cloak, target.getVisualArmor(VisualArmors.Cloak, false).getItemId());
			}
			activeChar.sendMessage("You have successfully copied " + target.getName() + "'s template!");
			activeChar.sendPacket(new UserInfo(activeChar));
			activeChar.sendPacket(new UserInfo(activeChar));
			activeChar.broadcastPacket(new CharInfo(activeChar));
			useVoicedCommand("dressme", activeChar, "");
		}
		else if (command.toLowerCase().startsWith("dressmestate"))
		{
			boolean turnOn = !activeChar.isUsingVisualArmors();
			activeChar.isUsingVisualArmors(turnOn);
			activeChar.sendMessage("You have successfully " + (turnOn ? "enabled" : "disabled") + " Visual Armors!");
			activeChar.sendPacket(new UserInfo(activeChar));
			activeChar.sendPacket(new UserInfo(activeChar));
			activeChar.broadcastPacket(new CharInfo(activeChar));
			useVoicedCommand("dressme", activeChar, "");
		}
		return true;
	}
	
	public String showItems(L2PcInstance activeChar)
	{
		String items = "";
		String sword = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.Sword, true) != null)
		{
			sword = activeChar.getVisualArmor(VisualArmors.Sword, true).getIcon();
		}
		String bow = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.Bow, true) != null)
		{
			bow = activeChar.getVisualArmor(VisualArmors.Bow, true).getIcon();
		}
		String pole = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.Pole, true) != null)
		{
			pole = activeChar.getVisualArmor(VisualArmors.Pole, true).getIcon();
		}
		String dual = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.Dual, true) != null)
		{
			dual = activeChar.getVisualArmor(VisualArmors.Dual, true).getIcon();
		}
		String bigSword = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.BigSword, true) != null)
		{
			bigSword = activeChar.getVisualArmor(VisualArmors.BigSword, true).getIcon();
		}
		String cloak = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.Cloak, true) != null)
		{
			cloak = activeChar.getVisualArmor(VisualArmors.Cloak, true).getIcon();
		}
		String armor = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.Armor, true) != null)
		{
			armor = activeChar.getVisualArmor(VisualArmors.Armor, true).getIcon();
		}
		String lHand = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.LHand, true) != null)
		{
			lHand = activeChar.getVisualArmor(VisualArmors.LHand, true).getIcon();
		}
		String legs = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.Legs, true) != null)
		{
			legs = activeChar.getVisualArmor(VisualArmors.Legs, true).getIcon();
		}
		String feet = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.Feet, true) != null)
		{
			feet = activeChar.getVisualArmor(VisualArmors.Feet, true).getIcon();
		}
		String gloves = "icon.NOIMAGE";
		if (activeChar.getVisualArmor(VisualArmors.Gloves, true) != null)
		{
			gloves = activeChar.getVisualArmor(VisualArmors.Gloves, true).getIcon();
		}
		items += "<table width=200>" + "<tr>" + "<td><img src=\"" + cloak + "\" width=32 height=32/></td>" + "<td><img src=\"" + armor + "\" width=32 height=32/></td>" + "<td><img src=\"" + lHand + "\" width=32 height=32/></td>" + "</tr>" + "<tr></tr><tr></tr>" + "<tr>" + "<td><img src=\"" + gloves + "\" width=32 height=32/></td>" + "<td><img src=\"" + legs + "\" width=32 height=32/></td>" + "<td><img src=\"" + feet + "\" width=32 height=32/></td>" + "</tr>" + "</table>";
		items += "<br><table width=250>" + "<tr>" + "<td><img src=\"" + sword + "\" width=32 height=32/></td>" + "<td><img src=\"" + bow + "\" width=32 height=32/></td>" + "<td><img src=\"" + pole + "\" width=32 height=32/></td>" + "<td><img src=\"" + dual + "\" width=32 height=32/></td>" + "<td><img src=\"" + bigSword + "\" width=32 height=32/></td>" + "</tr>" + "</table>" + "<br>";
		return items;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}