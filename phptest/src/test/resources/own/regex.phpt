--INFO--
regex problem in wordpress
--FILE--
<?
$tagregexp="";// ...
$comment_regex =
		  '!'           // Start of comment, after the <.
		. '(?:'         // Unroll the loop: Consume everything until --> is found.
		.     '-(?!->)' // Dash not followed by end of comment.
		.     '[^\-]*+' // Consume non-dashes.
		. ')*+'         // Loop possessively.
		. '(?:-->)?';   // End of comment. If not found, match all input.

	$shortcode_regex =
		  '\['              // Find start of shortcode.
		. '[\/\[]?'         // Shortcodes may begin with [/ or [[
		. $tagregexp        // Only match registered shortcodes, because performance.
		. '(?:'
		.     '[^\[\]<>]+'  // Shortcodes do not contain other shortcodes. Quantifier critical.
		. '|'
		.     '<[^\[\]>]*>' // HTML elements permitted. Prevents matching ] before >.
		. ')*+'             // Possessive critical.
		. '\]'              // Find end of shortcode.
		. '\]?';            // Shortcodes may end with ]]

	$regex =
		  '/('                   // Capture the entire match.
		.     '<'                // Find start of element.
		.     '(?(?=!--)'        // Is this a comment?
		.         $comment_regex // Find end of comment.
		.     '|'
		.         '[^>]*>'       // Find end of element.
		.     ')'
		. '|'
		.     $shortcode_regex   // Find shortcodes.
		. ')/s';

    $text = "t<html>est<? hi ?>";

	$textarr = preg_split( $regex, $text, -1, PREG_SPLIT_DELIM_CAPTURE | PREG_SPLIT_NO_EMPTY );

    echo   $regex;
    var_dump($textarr);
--EXPECT--
/(<(?(?=!--)!(?:-(?!->)[^\-]*+)*+(?:-->)?|[^>]*>)|\[[\/\[]?(?:[^\[\]<>]+|<[^\[\]>]*>)*+\]\]?)/s
array(4) {
  [0]=>
  string(1) "t"
  [1]=>
  string(6) "<html>"
  [2]=>
  string(3) "est"
  [3]=>
  string(8) "<? hi ?>"
}