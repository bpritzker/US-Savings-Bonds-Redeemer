# Everthing you need to redeem a large number US Savings Bonds

So my father passed away and left me with hundreds us physical US Savings bonds. My estate lawyer told me I need to 
find the date of death value for all them! 

As any good software engineer would say... why spend 4 hours typing in all the information when I could spend 8 hours 
developing a program to do all that for me!

This program is what I created. You take your bonds, SCAN them to images 
(one bond per image, something any scanner should be able to). Then there is one part of the program
that will take those images and use the Google Image Text Identifier to try to get all the text for the 
savings bonds. 

A second program will take the text dump from Google and try to identify all the necessary part of the
bonds like Serial Number and Issue Date.

Once you have those a third part screen scrapes the US Savings bond calculator to get the values on a 
specific date!

The text identifier works with around 80% accuracy and it has simple way to manually handle the rest.

This is crappy code but it works. If anyone else is interested in using it would be very happy 
to help them get it working and in the process take the time to clean it up.

Cheers,
Ben

