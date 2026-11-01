#!/bin/sh
#
# Checks the rule of this example: every value comes from a token, and every asset from the
# release. It reads the Kotlin source. An icon, an image or a token that left the release is not
# here, as it fails to compile.

set -u

root="$(cd "$(dirname "$0")/.." && pwd)"
source_dir="$root/app/src/main/kotlin"
failed=0

report() {
  echo "error: $1" >&2
  failed=1
}

# The lines of the source that match a pattern, without their comments and strings, so that copy
# is free to hold a number or a word. AppModel.kt holds the content of the example.
check() {
  # $1 what is wrong, $2 the pattern
  matches="$(find "$source_dir" -name '*.kt' ! -name 'AppModel.kt' -print0 |
    PATTERN="$2" xargs -0 awk '{
      line = $0
      sub(/^[[:space:]]*(\/\/|\/?\*).*/, "", line)
      gsub(/"[^"]*"/, "\"\"", line)
      if (line ~ ENVIRON["PATTERN"]) print FILENAME ":" FNR ": " line
    }')"
  if [ -n "$matches" ]; then
    report "$1:"
    echo "$matches" | sed "s|$root/||" >&2
  fi
}

check "a color that is no token" '[^a-zA-Z]Color\(|[^a-zA-Z]Color\.(White|Black|Gray|Red|Blue|Green|Yellow|Cyan|Magenta|Transparent)'
check "a system font" 'FontFamily\.(Default|SansSerif|Serif|Monospace|Cursive)|MaterialTheme\.typography'
check "a Material icon, which is no asset of the brand" 'material\.icons|Icons\.(Filled|Outlined|Default|Rounded|Sharp)'
check "a branch on a brand" 'BuildConfig|CHASSIS_BRAND'
check "a size that is no token" '[0-9]\.(dp|sp|em)'
check "a number that is no token" '(^|[^A-Za-z0-9_.])(0\.[0-9]+|[3-9]|[1-9][0-9]+)f?([^0-9A-Za-z_.]|$)'

# The source never names a brand or a family: both come from the tokens
for word in sinefil Montserrat Inter Lora Geist Cousine 'Fira Code'; do
  matches="$(grep -rnw --include='*.kt' "$word" "$source_dir")"
  if [ -n "$matches" ]; then
    report "the source names \"$word\":"
    echo "$matches" | sed "s|$root/||" >&2
  fi
done

if [ "$failed" -eq 0 ]; then
  echo "Every value is a token, and every asset is in the release."
fi
exit "$failed"
