/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.io.old;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;

/**
 * Graph writer for the GraphViz DOT format.
 *
 * @since 2007
 */
public class GraphWriterDOT implements GraphWriter
{
// Attribute
	
	/**
	 * The output.
	 */
	protected PrintStream out;
	
	/**
	 * Set of filtered node attributes.
	 */
	protected HashSet<String> nodeForbiddenAttrs = new HashSet<String>();
	
	/**
	 * Set of filtered edges attributes.
	 */
	protected HashSet<String> edgeForbiddenAttrs = new HashSet<String>();
	
	/**
	 * What element ?.
	 */
	protected enum What { NODE, EDGE, OTHER };
	
// Construction

	public GraphWriterDOT()
	{
		// NOP.
	}
	
// Command
	
	public void begin( String fileName, String graphName ) throws IOException
	{
		if( out != null )
			throw new IOException( "cannot call begin() twice without having called end() first." );
	
		out = new PrintStream( new BufferedOutputStream( new FileOutputStream( fileName ) ) );
		
		outputHeader( graphName );
	}

	public void begin( OutputStream stream, String graphName ) throws IOException
	{
		if( out != null )
			throw new IOException( "cannot call begin() twice without having called end() first." );
	
		out = new PrintStream( new BufferedOutputStream( stream ) );
		
		outputHeader( graphName );
	}
	
	protected void outputHeader( String graphName )
	{
		out.printf( "graph {%n" );
		
		if( graphName.length() > 0 )
			out.printf( "\tgraph [label=%s];%n", graphName );
	}
	
	public void end() throws IOException
	{
		if( out != null )
		{
			out.printf( "}%n" );
			out.flush();
			out.close();
			out = null;
		}
	}

	public void addNode( String id, Map<String, Object> attributes ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		out.printf( "\t%s", id );
		
		if( attributes != null )
			outputAttributes( attributes, What.NODE );
		
		out.printf( ";%n" );
	}

	@Deprecated
	public void changeNode( String id, Map<String, Object> attributes ) throws IOException
	{
		// Does not exist.
	}
	
	public void changeNode( String id, String attribute, Object value, boolean remove ) throws IOException
	{
		// Does not exist.
	}

	public void delNode( String id ) throws IOException
	{
		// Does not exist.
	}
	
	public void addEdge( String id, String node0Id, String node1Id, boolean directed, Map<String, Object> attributes ) throws IOException
	{
		if( out == null )
			throw new IOException( "use begin before using the writer!" );
		
		if( directed )
		     out.printf( "\t%s -> %s ", node0Id, node1Id );
		else out.printf( "\t%s -- %s ", node0Id, node1Id );
		
		if( attributes != null )
			outputAttributes( attributes, What.EDGE );
		
		out.printf( ";%n" );
	}
	
	@Deprecated
	public void changeEdge( String id, Map<String, Object> attributes ) throws IOException
	{
		// Does not exist.
	}
	
	public void changeEdge( String id, String attribute, Object value, boolean remove ) throws IOException
	{
		// Does not exist.
	}

	public void delEdge( String id ) throws IOException
	{
		// Does not exist.
	}

	public void step( double time ) throws IOException
	{
		// Does not exist.
	}
	
	@Deprecated
	public void changeGraph( Map<String, Object> attributes )
	{
		// Does not exist.
	}
	
	public void changeGraph( String attribute, Object value, boolean remove )
	{
		// Does not exist.
	}
	
	public void flush()
	{
		out.flush();
	}
	
// Utility
	
	protected void outputAttributes( Map<String,Object> attributes, What what ) throws IOException
	{
		out.printf( " [" );
		
		boolean first = true;
		
		for( String key: attributes.keySet() )
		{
			Object value = attributes.get( key );

			if( what == What.NODE )
			{
				if( ! nodeForbiddenAttrs.contains( key ) )
				{
					first = outputAttribute( key, value, first );
				}
			}
			else if( what == What.EDGE )
			{
				if( ! edgeForbiddenAttrs.contains( key ) )
				{
					first = outputAttribute( key, value, first );
				}
			}
			else
			{
				first = outputAttribute( key, value, first );
			}
			
		}
		
		out.printf( "]" );
	}
	
	protected boolean outputAttribute( String key, Object value, boolean first )
	{
		if( first )
		     out.printf( "\"%s\"=\"%s\"", key, value );
		else out.printf( ",\"%s\"=\"%s\"", key, value );
		
		return false;
	}
	
// Attribute filtering

	public void unfilterAllAttributes()
	{
		nodeForbiddenAttrs.clear();
		edgeForbiddenAttrs.clear();
	}

	public void unfilterAllEdgeAttributes()
	{
		edgeForbiddenAttrs.clear();
	}

	public void unfilterAllNodeAttributes()
	{
		nodeForbiddenAttrs.clear();
	}

	public void unfilterEdgeAttribute( String name )
	{
		edgeForbiddenAttrs.remove( name );
	}

	public void unfilterNodeAttribute( String name )
	{
		nodeForbiddenAttrs.remove( name );
	}

	public void filterEdgeAttribute( String name )
	{
		edgeForbiddenAttrs.add( name );
	}

	public void filterNodeAttribute( String name )
	{
		nodeForbiddenAttrs.add( name );
	}
}