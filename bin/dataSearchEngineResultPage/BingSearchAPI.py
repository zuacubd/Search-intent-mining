import requests # Get from https://github.com/kennethreitz/requests
import string

class BingSearchAPI():
    bing_api = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Composite?"
    
    def __init__(self, key):
        self.key = key

    def replace_symbols(self, request):
        # Custom urlencoder.
        # They specifically want %27 as the quotation which is a single quote '
        # We're going to map both ' and " to %27 to make it more python-esque
        request = string.replace(request, "'", '%27')
        request = string.replace(request, '"', '%27')
        request = string.replace(request, '+', '%2b')
        request = string.replace(request, ' ', '%20')
        request = string.replace(request, ':', '%3a')
        
        return request

    def search(self, sources, query, params):
        ''' This function expects a dictionary of query parameters and values.
            Sources and Query are mandatory fields.
            Sources is required to be the first parameter.
            Both Sources and Query requires single quotes surrounding it.
            All parameters are case sensitive. Go figure.

            For the Bing Search API schema, go to http://www.bing.com/developers/
            Click on Bing Search API. Then download the Bing API Schema Guide
            (which is oddly a word document file...pretty lame for a web api doc)
        '''
        request =  'Sources="' + sources    + '"'
        request += '&Query="'  + string.replace(str(query), '&', '%26') + '"'
        
        for key,value in params.iteritems():
            request += '&' + key + '=' + str(value)
        request = self.bing_api + self.replace_symbols(request)
        
        return requests.get(request, auth=(self.key, self.key))


if __name__ == "__main__":
    my_key = "om1RvP9qJO5yprKIdwjfvHOR2pY+JCCAmD0Sv1ibKAM"
    
    bing = BingSearchAPI(my_key)
    #params = {'ImageFilters':'"Face:Face"', 'Market':'"es-US"', '$format':'json', '$top': 50, '$skip': 0}
    params = {'Market':'"es-US"', '$format':'json', '$top': 50, '$skip': 0}

    file_out = open("/Users/iarcubd/Documents/research/project/IMine/src/data/searchEngineResultPage.txt", "w")
    file_in = open("/Users/iarcubd/Documents/research/project/IMine/src/data/topicSubtopicsString.txt", "r")

    file_out.write("<doc>\n")
    
    topicSubtopics = file_in.readlines();
    for line in topicSubtopics:
        
        start = line.find(">", 0, len(line))
        end = line.rfind("<", 0, len(line))
        length = (end - start);
        
        queryString = line[start+1:start+length]
 
        query_string = queryString
        
        print query_string
        print "retrieving..."
        
#        results =  bing.search('web', query_string, params).json()
        results = bing.search('web', query_string, params)

        print results.status_code
        results = results.json()
        
        last = line.rfind(">", 0, len(line))
        length = (last - end) + 1
        query_string = line[0:start+1] + queryString + line[end:end+length]
        file_out.write("<record>\n")
        file_out.write(query_string+"\n")
        
        print query_string
        print results["d"]["results"][0]["WebTotal"]
        
        hit_count = results["d"]["results"][0]["WebTotal"]
        hit_count = "<hit>" + hit_count +"</hit>"
        
        file_out.write(hit_count +"\n")
        file_out.write("<results>\n")
        
        result = results["d"]["results"][0]["Web"]
        
        for item in result:
            file_out.write("<item>\n<title>"+item["Title"]+"</title>\n<description>"+item["Description"]+"</description>\n<url>"+item["Url"]+"</url>\n<displayurl>"+item["DisplayUrl"]+"</displayurl>\n<id>"+item["ID"]+"</id>\n</item>\n")
        
        file_out.write("</results>\n")
        file_out.write("</record>\n")
    file_out.write("</doc>\n")
    file_out.close()
