#
# This is a utility script for generating the Async component of a GWT RPC service.
# You define the normal service, and then point this at it,
# and it will generate the Async service (keeping everything nice and in sync).
#
# (c) Michael Neale
#


OUTPUT = "RepositoryServiceAsync.java"
INPUT = "RepositoryService.java"


interface = IO.read(INPUT)
output = ""
interface.each_line { |line|
 groups = line.scan /\s+public\s+(.*?)\s+(.*?)\)\;\s*/
 if groups.size > 0 then 
	 if groups[0][0] == 'interface' then
		output = output + "\n" + line
 	else 
        	output = output + "\n" + "        public void " + groups[0][1] + ', AsyncCallback callback);'
 	end
 else 
	output = output + "\n" + line

 end


}

if File.exists? OUTPUT then File.delete(OUTPUT) end

f = File.new(OUTPUT, "w")
f.write output
f.close


