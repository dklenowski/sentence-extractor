#!/bin/bash
#set -x

base_dir=$1

if [ $# -eq 0 ]; then
  echo "Usage: run.sh <directory>"
  exit 1
fi

if [ ! -e "$base_dir" ]; then
  echo "base directory ($base_dir) does not exist?"
  exit 1
fi

if [[ ! $base_dir =~ \/$ ]]; then
  base_dir="${base_dir}/"
fi

echo "using base directory $base_dir"

for path in ${base_dir}*; do
  if [[ ! $path =~ [0-9]+\.txt$ ]]; then
    continue
  fi

  dst="${path}.clean"
  if [ -e $dst ]; then
    echo "skipping already processed $dst.."
    continue;
  fi

  echo "parsing $path.."
  java -server com.orbious.extractor.app.FileParser -i $path -o $dst

done
