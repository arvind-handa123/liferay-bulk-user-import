package com.knowarth.user.portlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.knowarth.user.model.UserMapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.WebKeys;


public class CSVReader {
  /** The Constant LOGGER. */
  private static final Log _log = LogFactoryUtil.getLog(CSVReader.class);
  
  /** Singleton instance. */
  private static CSVReader INSTANCE = new CSVReader();

  private static final Charset UTF8 = Charset.forName("UTF-8");
  
  private CSVReader() {
  }

  public static CSVReader getInstance() {
    return INSTANCE;
  }

  final CellProcessor[] processors = new CellProcessor[] {
		  new NotNull(), new NotNull(), new NotNull(), new NotNull(), new NotNull(), 
		  new Optional(new ParseBool()), null, new Optional(new ParseDate("dd-MM-yyyy")),
		  null, null, null, null, null, 
		  null, null, null, null, null};

  public List<UserMapper> readUsers(final ActionRequest request, String Fname) {
    ICsvBeanReader inFile = null;

    List<UserMapper> users = new ArrayList<UserMapper>();
    
    if(_log.isDebugEnabled()) {
      _log.debug("Try to open the uploaded csv file");
    }
      String urldecoded = CSVReader.decodeUrl(Fname.replace('/', File.separatorChar));
      	
    try {
    	String portletInstanceId = (String) request.getAttribute(WebKeys.PORTLET_ID);
    	PortletPreferences preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletInstanceId);
    	String customFields = preferences.getValue("customFields","");
    	String[] custFields = customFields.split(",");
    	int k = 0;
		String[] cFields = new String[] {
				null, null, null, null, null,
				null, null, null, null, null
				};
		String cfieldName = "";
    	for (int j = 0; j < 10 && j < custFields.length; j++) {
			k = j + 1;
			cfieldName = "customField" + k;
    		cFields[j] = cfieldName;
    	}
    	for(int z = k; z < 10; z++){
    		cFields[z] = null;
    	}
        final String[] header = new String[18];
        header[0] = "username";
        header[1] ="email";
        header[2] = "firstName"; 
        header[3] = "lastName";
        header[4] ="password";
        header[5] = "male"; 
        header[6] = "jobTitle"; 
        header[7] = "birthday";
    	for (int j = 8; j < 18 ; j++) {
    		header[j] = cFields[j-8];
    	}
    	if(_log.isDebugEnabled()) {
        	_log.debug("Header for mapping: "+ Arrays.toString(header));
        }
    	inFile = new CsvBeanReader(new FileReader(urldecoded), CsvPreference.STANDARD_PREFERENCE);
       
    	// header verify
    	final String[] header_temp = inFile.getHeader(true);
    	List<String> expectedHeaders = Arrays.asList("username","email","firstName","lastName",
        		"password","male","jobTitle","birthday"
        		,"customField1","customField2","customField3","customField4","customField5"
        		,"customField6","customField7","customField8","customField9","customField10"
        		);

        if (!Arrays.asList(header_temp).containsAll(expectedHeaders)){
            if(_log.isDebugEnabled()) {
            	_log.debug("Header in CSV file: "+ Arrays.toString(header_temp));
                _log.debug("Expected header not found in the CSV file.");
            }

        	SessionErrors.add(request,"expected-header-not-found-in-the-csv-file");
        	try {
                inFile.close();
              } catch (IOException e) {
            	  _log.error(e);
              }
        	return null;
        }

        if(_log.isDebugEnabled()) {
            _log.debug("Reading users with properties: " + Arrays.toString(header) + " from CSV file.");
        }
        UserMapper user;
        boolean goLoop;
        goLoop = true;
        long curRow = 0;
        while (goLoop) {
        	try {
        		curRow += 1;
	        	user = inFile.read(UserMapper.class, header, processors);
	        	if (user != null) {
	        		users.add(user);
	        	} else {
	        		goLoop = false;
	        	}

        	} catch (SuperCsvConstraintViolationException eCV) {
            	_log.error("NON right VALUE ENCOUNTERD ON ROW "+ inFile.getRowNumber() + " --- "+  eCV.getMessage());

        		SessionErrors.add(request,"non_right_value_ecountered_on_row");
            	request.setAttribute("error_row", curRow);
            	try {
                    inFile.close();
                  } catch (IOException e) {
                	  _log.error(e);
                  }
            	return null;
        	} catch (SuperCsvCellProcessorException ePE){
        		 _log.error("PARSER EXCEPTION ON ROW "+inFile.getRowNumber() + " --- "+  ePE.getMessage());
         		SessionErrors.add(request,"parser_exception_on_row");
            	request.setAttribute("error_row", curRow);
            	try {
                    inFile.close();
                  } catch (IOException e) {
                	  _log.error(e);
                  }
            	return null;

        	} catch (SuperCsvException ex){
        		 _log.error("ERROR ON ROW "+inFile.getRowNumber() + " --- "+  ex.getMessage());
         		SessionErrors.add(request,"error_on_row");
            	request.setAttribute("error_row", curRow);
            	try {
                    inFile.close();
                  } catch (IOException e) {
                	  _log.error(e);
                  }
            	return null;

        		 }

        }
        if(_log.isDebugEnabled()) {
            _log.debug(users.size() + " users were read from CSV file.");
        }
      } catch (FileNotFoundException fnfe) {
    	  if(_log.isErrorEnabled()) {
    		  _log.error("Can't find CSV file with users " + fnfe);
    	  }
      } catch (IOException ioe) {
  		_log.error(ioe);
      } catch (PortalException e) {
		_log.error(e);
	} catch (SystemException e) {
		_log.error(e);
	} finally {
        try {
          inFile.close();
        } catch (IOException e) {
        	_log.error(e);
        }
      }

      return users;
  }
  static String decodeUrl(String url) {
	        String decoded = url;
	        if (url != null && url.indexOf('%') >= 0) {
	            int n = url.length();
	            StringBuffer buffer = new StringBuffer();
	            ByteBuffer bytes = ByteBuffer.allocate(n);
	            for (int i = 0; i < n;) {
	                if (url.charAt(i) == '%') {
	                    try {
                        do {
	                            byte octet = (byte) Integer.parseInt(url.substring(i + 1, i + 3), 16);
	                            bytes.put(octet);
	                            i += 3;
	                        } while (i < n && url.charAt(i) == '%');
	                        continue;
	                    } catch (RuntimeException e) {
	                        // malformed percent-encoded octet, fall through and
	                        // append characters literally
	                    } finally {
	                        if (bytes.position() > 0) {
	                            bytes.flip();
	                            buffer.append(UTF8.decode(bytes).toString());
	                            bytes.clear();
	                        }
	                    }
	                }
	                buffer.append(url.charAt(i++));
	            }
	            decoded = buffer.toString();
	        }
	        return decoded;
	    }
}