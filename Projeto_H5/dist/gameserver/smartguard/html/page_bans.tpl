<font color="919191">There are</font> %count% <font color="919191">banned HWID</font>
<table width=275>
    <tr>
        <td><edit var="srch" width=220 height=14></td>
        <td><button value="search" action="bypass -h admin_sg_bans 0 $srch" width=40 height=19 back="L2UI_ch3.smallbutton2" fore="L2UI_ch3.smallbutton2"></td>
    </tr>
</table>
<br>
<center>
    <table width="100">
        <tr>
            <td><button width=14 height=14 action="bypass -h admin_sg_bans %page_prev% %query%" back="L2UI_ch3.prev1_down" fore="L2UI_ch3.prev1"></td>
            <td>%page_cur% / %page_max%</td>
            <td><button width=14 height=14 action="bypass -h admin_sg_bans %page_next% %query%" back="L2UI_ch3.next1_down" fore="L2UI_ch3.next1"></td>
        </tr>
    </table>
</center>
<br>
%records%