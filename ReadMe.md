# About WITS #

WITS is a Wiki to Structured Markup converter that can convert wiki files of different markups (Confluence and JSPWiki) to a structured format like DocBook v5 XML. Unlike other XML/SGML encoders, WITS does not produce XML blocks through in-memory tree or DOM structures. WITS produces structured text blocks by passing the input wiki text through multiple pre-defined wiki interceptors to achieve an almost perfect conversion. The trick lies in considering the wiki text blocks as a continuous string and then applying 'wiki filters' over the text to produce an unclean XML. This happens as several passes (~40-50). At one point, the wiki text gets completely converted to XML. This is a strange technique but ensures WITS to be effective when handling complex wiki patterns. This is necessary because the wiki text is considered the most unclean a structure.

Why is WITS faster? Pre-defined wiki patterns are applied directly on the optimized text buffer (containing the input blocks) to add and manipulate the string in such a way that the output is an unclean XML (mixture of wiki and XML tags). So at least theoretically there are some rare cases when the output produced by the WITS parser may not be a valid XML file. This is very rare and if you had hit that case, either you have a completely chaotic wiki or you have a completely chaotic wiki (no other reason!).

WITS is a java tool and is available as an executable jar file.

# Building WITS Parser from the Source #

For building the WITS parser from source, you need to have ANT and Java SE SDK installed in your system. The following instructions shows you how you can get the source from the SVN repository and how you can build it yourself.

# WITS Binary #
  1. http://wits-parser.googlecode.com/svn/trunk/bin/Confluence/WITS.jar
  1. http://wits-parser.googlecode.com/svn/trunk/bin/JSPWiki/WITS.jar

**Note** The binaries are compiled with Java 1.6 compiler. If you have a Java runtime less than 1.6, you may get the following error:

```
Exception in thread "main" java.lang.UnsupportedClassVersionError: Bad version number in .class file
```

So you need to have a Java runtime > 1.6 to execute the jar file.

## Get the Source ##

  1. `svn checkout http://wits-parser.googlecode.com/svn/trunk/ wits-parser-read-only`
  1. `cd wits-parser/trunk/WITS_Conf_src`
  1. `ant buildWITS` (to build the Confluence Parser)

or

  1. `svn checkout http://wits-parser.googlecode.com/svn/trunk/ wits-parser-read-only`
  1. `cd wits-parser/trunk/WITS_JSPWiki_src`
  1. `ant buildWITS` (to build the JSPWiki Parser)

`buildWITS` is the name of the ANT target defined in the `build.xml` file.

When you are done with the building, WITS.jar file will be created. Here is the sample output:

```
$ ls
build.xml  org
$ ant buildWITS
Buildfile: build.xml

compile:
    [javac] Compiling 36 source files

jar:
      [jar] Building jar: ../wits-parser-read-only/WITS_Conf_src/WITS.jar

cleanup:

buildWITS:

BUILD SUCCESSFUL
Total time: 2 seconds
```

**Remember** building will be clean only when you use Sun JDK 1.6 or higher. Jikes doesn't work quite well with Generics. If you get `javac` warning and if build fails, set the `JAVA_HOME` variable to the correct Sun JDK 1.6 directory path and try again.

# Running  the tool #

```
java -jar WITS.jar sample0.txt
```

where `sample0.txt` is a text file that contains valid Confluence/JSPWiki wiki markup text. NOTE You can also provide multiple wiki pages as source:

```
java -jar WITS.jar sample1.txt sample2.txt
```

You can also use the `--force` option to force the wiki conversion for an alien Wiki format. If there is a parser available for your wiki, use that. If you provide --test option, converted text will not be stored anywhere.

Pass `--config configFile` to the parser to customize the placeholder text added by the parser.

For example,

```
java -jar WITS.jar --config wits.config input.txt
```

```
//This is the format for the WITS Config file
WITS_BookTitle = Enter your book title here
WITS_ChapterTitle = Enter your chapter title here
WITS_ChapterHighlights = Enter chapter highlights here
WITS_AuthorName = Author
WITS_PubsNumber = 1234
WITS_ReleaseInfo =1.0
WITS_PubDate = 2008
WITS_PubName = Some Company Inc.
WITS_CopyrightYear = 2008
WITS_BookAbstract = Enter abstract here
WITS_LegalNotice = Enter legal notice
WITS_DanglerSectionSummary = Enter section summary here
WITS_DanglerSectionTitle = Enter section title here
WITS_DanglerTableTitle = Enter table title here
WITS_WIKISiteBaseURL = http//wikis.company.com/mywiki/
```

The following output shows the command line usage:

```
Usage:

java -jar WITS.jar <inputFile> or <inputDir>

Other Options:
[--docbook]    - Enable DocBook Output.
[--test]    - Do not write output to file.
[--force]    - Force conversion for alien wiki formats.
[--outputdir <path>]    - Provide the path to output Dir.
[--compress]    - Creates a compressed archive of the output.
[--silent]    - Do not print output.
[--config <configFile>]    - Provide a configuration file.
[--target attr1,attr2..]    - Provide the WITS Targets to Show/Hide Content.


Example 1: Create SolBook Output.
 java -jar WITS.jar input1.txt
 java -jar WITS.jar inputdir
 java -jar WITS.jar input1.txt input2.txt input3.txt
 java -jar WITS.jar --config WITS.props input1.txt
 java -jar WITS.jar --config WITS.props --outputdir /MyDir input1.txt
 java -jar WITS.jar --config WITS.props --outputdir /MyDir --compress input1.txt

Example 2: Create DocBook Output.
 java -jar WITS.jar --docbook input1.txt
 java -jar WITS.jar --docbook inputdir
 java -jar WITS.jar --docbook input1.txt input2.txt input3.txt
 java -jar WITS.jar --docbook --config WITS.props input1.txt
 java -jar WITS.jar --docbook --config WITS.props --outputdir /MyDir input1.txt
 java -jar WITS.jar --docbook --config WITS.props --outputdir /MyDir --compress input1.txt

```

## Conditional Outputs ##
You can hide or make text blocks available for WITS processing by using 'targets'. WITS targets are supported for Confluence wiki pages only.

In your wiki page, surround the conditional texts with comment blocks:

```
{excerpt:hidden=true}
#WITSTarget:START=target1
{excerpt}
...
conditional text here
...
{excerpt:hidden=true}
#WITSTarget:END=targte1
{excerpt}
```

The above WITS target handlers instruct the WITS parser to parse the enclosed block of text only when the conversion is invoked with that particular targets:

```
java -jar WITS.jar input1.txt --target target1
```

target1 is optional. If you do not provide any option, all conditions are ignored. You can specify multiple targets:

```
{excerpt:hidden=true}
#WITSTarget:START=target1,target2
{excerpt}
...
conditional text here. This will
be parsed if any of the targets matches.
...
{excerpt:hidden=true}
#WITSTarget:END=targte1,target2
{excerpt}
```

In the above case, the enclosed text will be parsed if the parser is invoked with either target1 or target2 option:

```
java -jar WITS.jar input1.txt --target target1
```

In the above case, enclosed block is processed.

```
java -jar WITS.jar input1.txt --target target2
```

In the above case, enclosed block is processed.

```
java -jar WITS.jar input1.txt --target target3
```

In the above case, enclosed block is not processed.

# What the tool can do? #

In a summary,

  1. Clean conversion of lists/nested lists (both ordered and itemized).
  1. Supports code, no-parse content.
  1. Handle quotes, note, tip, caution etc.
  1. Heading Orphan/Dangler Handling.
  1. Clean conversion of headings.
  1. Auto flattening and auto insertion of headings.
  1. Support for tables
  1. Supports inline markups like emphasis, links, and literals.
  1. Pattern based lookup based on context and preset wiki rules.
  1. Cloud parsing to overcast text. Faster parsing time.
  1. Performance - Faster string search algo., non-synchronized string buffered search, native I/O etc.

# Some Examples for Clean Conversion #

Apart from straight forward pattern based conversion, some complex cases are handled.

## Case1 - Handling Section Danglers ##

```
START_OF_WIKI
Some para here
h1. Heading1
Heading 1 content
```

TO

```
<sect1><title>Auto title</title>
	some para here
</sect1>
<sect1><title>Heading1</title>
	Heading 1 content
</sect1>
```

## Case 2 - Inserting Missing Sections ##

```
h1. Heading1
Heading 1 content
h3. Heading3
Heading 3 content
```

TO

```
<sect1><title>Heading1</title>
	Heading 1 content
	<sect2><title>Auto title</title>
		Auto content
		<sect3><title>Heading3</title>
			Heading 3 content
		</sect3>
	</sect2>
</sect1>
```

## Case 3 - Flattening Sections ##

```
h2. Heading2
Heading 2 content
h6. Heading6
Heading 6 content
```

TO

```
<sect2><title>Heading2</title>
	Heading 2 content
</sect2>
<sect2><title>Heading6</title>
	Heading 6 content		
</sect2>
```

## Case 4 - Flattening Lists ##

```
* listitem1
** Nested (lititem1)1
** Nested (lititem1)2
*** Nested ((lititem1)2)1
** Nested (lititem1)3
*** Nested ((lititem1)3)1
**** Nested (((lititem1)3)1)1
```

TO

```
<orderedlist>
	<listitem><para>listitem1</para></listitem>
	<listitem>
		<orderedlist>
			<listitem><para>Nested (lititem1)1</para></listitem>
			<listitem><para>Nested (lititem1)2</para></listitem>
			<listitem>
				<orderedlist>
					<listitem><para>Nested ((lititem1)2)1</para></listitem>
				</orderedlist>	
			</listitem>
			<listitem><para>Nested (lititem1)3</para></listitem>
			<listitem>
				<orderedlist>
					<listitem><para>Nested ((lititem1)3)1</para></listitem>
					<listitem><para>Nested (((lititem1)3)1)1</para></listitem>
				</orderedlist>	
			</listitem>
		</orderedlist>
	</listitem>
</orderedlist>
			
```

# WITS Tips #

Wiki is a free-for-all format. While you are free to throw in your content, just make sure that you follow these guidelines so that WITS conversion is smooth.

  1. Always pass an external configuration file to the WITS parser using the `--config` option to avoid the default (placeholder) content being added.
  1. Do not be Emphasis fetish. Use Bold/Italic only when it is necessary.
  1. Do not have literal texts inside headings or links. It does not make sense.
  1. Table entries should not have rich structure like bulleted lists. Same holds good for notes, warning, and tips.
  1. Use section heading styles responsibly. These styles should not be used for controlling the section fonts. Some wiki pages contains only h5s and h6s. This is bad structuring.
  1. Don't write in an un-structured and heavily cross-referred fashion. Let us admit it. You need a branded PDF and that is a document having structured chapters and each chapter have sections organized.
  1. Since this parser is a standalone tool it has no privilege of automatically extracting excerpt text blocks for your Wiki page. So you need to add the excerpt text inline before you use this tool.
  1. Do not use \ escape character throughout the Wiki page. If you want the parser to ignore the markup, just enclose the element in an inline literal markup.
  1. Figures are evil.
  1. There is no such thing as a 4 way nested lists ####. This is ugly.
  1. There is no such thing as a sect4. It used be there for people that can't structure their content properly.
  1. For internal Wiki links to be rendered correctly, always pass the base URL from an external config file.
  1. Use the `--test` option to check if your input Wiki text passes some WITS cases:

```
java -jar WITS_Conf.jar --test --config ws.wits test.txt 

WITS [Confluence Parser] - V0.7.2
Applying Wiki Filters...
Output stream disabled for testing...
Reading WITS Config file: ws.wits
Building DocBook Content...

Running WITS cases on test.txt

   XML Validity [Fatal]---------------PASS
   Wiki LeftOver [Warning]------------PASS
   Default Content [Warning]----------PASS
   Internal Content [Warning]---------PASS

    Pass % 100
    Fail % 0
```

The parser produces a DocBook V5 XML block from your Wiki using some Wiki filters and will try to validate the block against some test cases. If you get a FAIL for any non-fatal test, that is still OK. Nothing to panic. If the XML validity test fails, send a mail to `fermatjen@yahoo.com`.