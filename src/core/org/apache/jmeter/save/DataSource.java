/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 * "Apache JMeter" must not be used to endorse or promote products
 * derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 * "Apache JMeter", nor may "Apache" appear in their name, without
 * prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jmeter.save;


import java.io.IOException;
import java.util.Collection;

import org.apache.jmeter.samplers.SampleResult;


/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public interface DataSource
{
    public final static int BASE_INFO_MASK = 1;
    public final static int EXTRA_INFO_MASK = 1 << 1;
    public final static int SUB_RESULTS_MASK = 1 << 2;
    public final static int RESPONSE_MASK = 1 << 3;
    public final static int REQUEST_DATA_MASK = 1 << 4;
    public final static int ASSERTION_RESULTS_MASK = 1 << 5;

    public final static int APPEND = 1;
    public final static int OVERWRITE = 2;

    /**
     * Opens a file for recording sample results.
     * @param filename The name of the file to record to.  Any attempt to open a file that's
     * already been opened will result in an exception
     * @param mode Mode indicates whether the file is opened for appending data to the
     * end of the file or overwriting the file contents.
     * @param contentMask - A mask defining what data is recorded.  The options are:<br>
     * BASE_INFO_MASK = all the basic data points (label, time, success)<br>
     * EXTRA_INFO_MASK = Various miscellaneous data (thread_name, timestamp, response code,
     * response message, data type)<br>
     * SUB_RESULTS_MASK = Whether to include sub results in the recording.  The level of detail 
     * of the sub results will match that chosen for the main result<br>
     * RESPONSE_MASK = Whether to store the response data<br>
     * REQUEST_DATA_MASK = Records the request data<br>
     * ASSERTION_RESULTS_MASK = Record the messages from assertions
     * 	
     */
    public void openSource(int mode, int contentMask) throws IOException;

    /**
     * Closes a file that had been opened for recording.  
     * @param filename Name of file to close.
     */
    public void closeSource() throws IOException;

    /**
     * Load a file of previously recorded sample results and return them all in a collection.
     * @return Collection
     * @throws JMeterSaveException
     */
    public Collection loadLog() throws IOException;

    /**
     * Load a number of samples from the data source, starting from the next sample.
     * @param length
     * @return Collection
     * @throws IOException
     */
    public Collection loadLog(int length) throws IOException;

    /**
     * Save a SampleResult object to the specified file.  The file must have been initialized
     * with a (link beginRecording(String,int,int,int)) call.
     * @param filename
     * @param result
     * @throws JMeterSaveException
     */
    public void recordSample(SampleResult result) throws IOException;

}
