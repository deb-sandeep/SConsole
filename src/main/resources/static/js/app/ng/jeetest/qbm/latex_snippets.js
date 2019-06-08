function LatexSnippets() {
	
	var snippets = {
		piecewise : 
"$$\n\
f(n) = \n\
\\begin{cases} \n\
n/2,  & \\text{if $n$ is even} \\\\\\\\\\\\ \\\\\\\\\\\\ \n\
3n+1, & \\text{if $n$ is odd} \n\
\\end{cases}\n\
$$\n",

		matrix :
"$$\n\
\\begin{vmatrix}\n\
1 & x & x^2 \\\\\\\\\\\\ \n\
1 & y & y^2 \\\\\\\\\\\\ \n\
1 & z & z^2 \\\\\\\\\\\\ \n\
\\end{vmatrix}\n\
$$\n",

		d2ydx2 :
"\\frac{d^2}{dx^2}",

		limit:
"$$\n\
\\lim_{x \\to 0} \\frac{x}{y}\n\
$$\n"

	} ;
	
	this.getSnippet = function( snippetId ) {
		return snippets[snippetId] ;
	}
}