#!/usr/bin/perl
#
# A script that runs pre-processing on a directory containing
# Project Gutenberg files.
# This script removes the start and end Gutenberg tags 
# from the document and writes the modified file to a difference directory.
#
#
use strict;
use warnings;
use Getopt::Std ();
use File::Spec ();

#
# globals
#
our $debug = 1;

#
# methods
#
sub usage {
  print <<EOF
  Usage: pre_process_gutenberg.pl -d <input_directory> -o <output_directory>
  -d <input_directory>    Project Gutenberg directory containing .txt files.
  -o <output_directory>   Directory to put processed output files.
  
EOF
;
  exit(1);
}

#
# main
#
our ( $opt_d, $opt_o );

Getopt::Std::getopts('d:o:');
if ( !$opt_d || !-d($opt_d) ) {
  usage();
} elsif ( !$opt_o || !-d($opt_o) ) {
  usage();
}

if ( !opendir(DIR, $opt_d) ) {
  print "Error: Failed to open directory $opt_d : $!\n";
  exit(1);
}

my @files = readdir(DIR);
closedir(DIR);

my $outputfile;
my $path;
my $fndStart;
my $fndEnd;
my $lineCt;
my $processedCt;

foreach my $file ( @files ) {
  next if ( $file !~ m/\.txt$/ || $file =~ m/_clean/ );
  
  $path = File::Spec->catfile($opt_d, $file);
  
  $outputfile = File::Spec->catfile($opt_o, $file);
  $outputfile =~ s/\.txt$//;
  $outputfile .= '_clean.txt';

  if ( -e($outputfile) ) {
    print "Info: Skipping $file (output file $outputfile exists)\n";
    next;
  }
  
  print "Info: Processing $file to $outputfile\n";

  if ( !open(INP, $path) ) {
    print "Error: Failed to open input file $path : $!\n";
    exit(1);
  }
  
  if ( !open(OUT, ">$outputfile") ) {
    print "Error: Failed to open output file $outputfile : $!\n";
    exit(1);
  }

  $fndStart = 0;
  $fndEnd = 0;
  $lineCt = 0;
  $processedCt = 0;
  
  while ( <INP> ) {
    $lineCt++;
    if ( $_ =~ m/START OF THIS PROJECT GUTENBERG/ || 
      $_ =~ m/START OF THE PROJECT GUTENBERG/ ) {
      $fndStart++;
      next;
    } elsif ( $_ =~ m/END OF THIS PROJECT GUTENBERG/ || 
      $_ =~ m/End of the Project Gutenberg EBook/ ||
      $_ =~ m/END OF THE PROJECT GUTENBERG EBOOK/ ) {
      $fndEnd++;
      last;
    } elsif ( $fndStart ) {
      $processedCt++;
      print OUT $_;
    }
  }
  
  close(INP);
  close(OUT);
  
  if ( !$fndStart ) {
    print "Error: Failed to find start for $file?\n";
  } elsif ( !$fndEnd ) {
    print "Error: Failed to find end for $file?\n";
  } else {
    print "Info: Found $lineCt lines and processed $processedCt lines in $file\n";
  }
}