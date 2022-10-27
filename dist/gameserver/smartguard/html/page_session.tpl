<table bgcolor=000000>
    <tr>
        <td><font color="919191">HWID:</font> %hwid%</td>
    </tr>
    <tr>
        <td><font color="919191">Players in game:</font> %online%</td>
    </tr>
    <tr>
        <td>
            <table width=270>
                <tr>
                    <td><font color="919191">Player</font></td>
                    <td><font color="919191">Acc</font></td>
                    <td><font color="919191">Actions</font></td>
                </tr>
                %records%
            </table>
        </td>
    </tr>
</table>
<br>
<table border=0>
    <tr>
        <td><button value="Ban HWID" action="bypass -h admin_sg_ban hwid %hwid%" width=100 height=21 back="L2UI_ch3.bigbutton3_down" fore="L2UI_ch3.bigbutton3"></td>
        <td><button value="Kick all" action="bypass -h admin_sg_kick_session %sid%" width=100 height=21 back="L2UI_ch3.bigbutton3_down" fore="L2UI_ch3.bigbutton3"></td>
    </tr>
</table>