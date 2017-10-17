#!/usr/bin/perl -w
use strict;
use warnings;
use File::Find;

my $directory = $ENV{"HOME"};
my $file_count;

print "Scan directory: ", $directory, "\n";

find( sub { -f && $file_count++ }, $directory );

print "\nTotal files: ", $file_count, "\n";