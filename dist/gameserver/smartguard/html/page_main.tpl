<font color="DBC309">Manage players:</font>
<table height=25>
    <tr>
        <td><center><edit var="player_cmd" width=265 height=15></center></td>
    </tr>
</table>
<table>
    <tr>
        <td><button value="Search" action="bypass -h admin_sg_find player $player_cmd" width=85 height=21 back="L2UI_ch3.bigbutton3_down" fore="L2UI_ch3.bigbutton3"></td>
        <td><button value="Ban HWID" action="bypass -h admin_sg_ban player $player_cmd" width=85 height=21 back="L2UI_ch3.bigbutton3_down" fore="L2UI_ch3.bigbutton3"></td>
    </tr>
</table>
<br>

<font color="DBC309">Actions with HWID:</font>
<table height=25>
    <tr>
        <td><center><edit var="hwid_cmd" width=265 height=15></center></td>
    </tr>
</table>
<table>
    <tr>
        <td><button value="Ban HWID" action="bypass -h admin_sg_ban hwid $hwid_cmd" width=85 height=21 back="L2UI_ch3.bigbutton3_down" fore="L2UI_ch3.bigbutton3"></td>
        <td><button value="Unban HWID" action="bypass -h admin_sg_unban $hwid_cmd" width=85 height=21 back="L2UI_ch3.bigbutton3_down" fore="L2UI_ch3.bigbutton3"></td>
        <td><button value="Search" action="bypass -h admin_sg_find hwid $hwid_cmd" width=85 height=21 back="L2UI_ch3.bigbutton3_down" fore="L2UI_ch3.bigbutton3"></td>
    </tr>
</table>