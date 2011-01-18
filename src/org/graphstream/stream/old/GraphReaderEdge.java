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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.graphstream.graph.NotFoundException;
import org.graphstream.io.GraphParseException;

/**
 * Reader for the "edge" graph format.
 *
 * <p>
 * The edge graph format is a very simple and lightweight format where
 * each line describe an edge by giving two node names. The nodes are
 * created implicitly.
 * </p>
 * 
 * <p>
 * This reader also understand the derivative format where a line contains
 * a first node name, followed by several node names separated by spaces.
 * In this case it links the first node with all other node name following.
 * </p>
 *
 * @author Antoine Dutot
 * @author Yoann Pign�
 * @since 2007
 */
public class GraphReaderEdge extends GraphReaderBase
{
// Attribute
	
	/**
	 * Allocator for edge identifiers.
	 */
	protected int edgeid = 0;
	
	/**
	 * By default, consider edges as undirected.
	 */
	protected boolean directed = false;
	
// Construction

	/**
	 * New reader for the "edge" format.
	 */
	public GraphReaderEdge()
	{
		this( false );
	}
	
	/**
	 * New reader for the "edge" format.
	 * @param edgesAreDirected If true edges are considered directed.
	 */
	public GraphReaderEdge( boolean edgesAreDirected )
	{
		directed = edgesAreDirected;
	}
	
// Commands
	
	@Override
	protected void continueParsingInInclude() throws IOException, GraphParseException
	{
		// Should not happen, EDGE files cannot be nested.
	}

	@Override
	public boolean nextEvents() throws GraphParseException, IOException
	{
		String id1 = getWordOrNumberOrStringOrEolOrEof();
		
		if( id1.equals( "EOL" ) )
		{
			// Empty line.
		}
		else if( id1.equals( "EOF" ) )
		{
			return false;
		}
		else
		{
			String id2 = getWordOrNumberOrStringOrEolOrEof();
			
			while( ! id2.equals( "EOL" ) )
			{
				if( ! id1.equals( id2 ) )
				{
					for( GraphReaderListener listener: listeners )
						listener.edgeAdded( Integer.toString( edgeid++ ),
							id1, id2, directed, null );				
					for( GraphReaderListenerExtended listener: listeners2 )
						listener.edgeAdded( Integer.toString( edgeid++ ),
							id1, id2, directed, null );
				}
				
				id2 = getWordOrNumberOrStringOrEolOrEof();
			}
		}
		
		return true;
	}
	
	@Override
	public void begin( String filename ) throws NotFoundException, GraphParseException, IOException
	{
		super.begin( filename );
		init();
	}
	
	protected void init() throws GraphParseException, IOException
	{
		st.eolIsSignificant( true );
		st.commentChar( '#' );
	}

	@Override
	public void begin( InputStream stream ) throws GraphParseException, IOException
	{
		super.begin( stream );
		init();
	}

	@Override
	public void begin( Reader reader ) throws GraphParseException, IOException
	{
		super.begin( reader );
		init();
	}

	public boolean nextStep() throws GraphParseException, IOException
	{
		return nextEvents();
	}

	@Override
	public void end() throws GraphParseException, IOException
	{
		super.end();
	}

	@Override
	protected void parseGraph() throws GraphParseException, IOException
	{
		boolean loop = true;
		
		while( loop )
		{
			loop = nextEvents();
		}
		
		end();
	}
}