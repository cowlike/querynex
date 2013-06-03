querynex
========

Simple Groovy app to query Nexuiz servers. I am playing with some ideas to have a simple notifier for when players join a server I'm interested in. I started with [this code](https://code.google.com/p/tortilla/) as my base, which I found when I was searching online for docs on the server protocol. 

The Tortilla code is very nice but I just wanted the parts to query the server since I'm not really interested in a GUI tool. His code really is quite nice though.

Made some changes to publish to a "notifier" which currently only includes twitter or console. Default is to throw away these publish messages. Probably should consolidate console publish with the regular console output! Next time...

usage
=====
java -jar querynex-xxx.jar -h
<pre>
usage: Possible options
 -w                Watch server forever. Has priority over number of
                   iterations (-i)
 -a                Only show active players, not specs
 -b                Also show bots in the player list
 -f <filename>     Read a file of lines with ip:port
 -h                This screen
 -i <iterations>   Number of times to check the servers
 -s <ip:port>      Specify the server ip:port

Passing no arguments will query 127.0.0.1:26000 a single time.

Example for file input:
<pre>
# format is ip:port
# Empty lines or lines starting with '#' are ignored.

# 213.141.136.246:26003
# 94.23.20.72:26000

198.58.96.171:26000
</pre>

[latest download](https://dl.dropboxusercontent.com/u/510237/nexuiz/querynex-0.0.14-jar-with-dependencies.jar)
