#!/usr/bin/expect -f

spawn sudo prosodyctl passwd [lindex $argv 0]

expect "Enter new password: "

send -- "[lindex $argv 1]\r"

expect "Retype new password: "

send -- "[lindex $argv 1]\r"

expect eof
