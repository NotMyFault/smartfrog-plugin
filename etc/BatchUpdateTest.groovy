/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
import hudson.model.*
import builder.smartfrog.*
import hudson.tasks.*
import java.util.regex.Pattern

/*
def projects = Hudson.instance.getAllItems(Project.class)

for(project in projects) {
  if ("${project.name}".contains("eap-5x-failover-http-random")) {
   //println "\n* ${project.name}"
      print "\"${project.name}\","
   }
}
 */

//def jobs = ["eap-5x-failover-http-jvmkill-buddy-async","eap-5x-failover-http-jvmkill-buddy-sync","eap-5x-failover-http-jvmkill-total-async","eap-5x-failover-http-jvmkill-total-sync","eap-5x-failover-http-random-jvmkill-buddy-async","eap-5x-failover-http-random-jvmkill-buddy-sync","eap-5x-failover-http-random-jvmkill-total-async","eap-5x-failover-http-random-jvmkill-total-sync","eap-5x-failover-http-random-shutdown-buddy-async","eap-5x-failover-http-random-shutdown-buddy-sync","eap-5x-failover-http-random-shutdown-total-async","eap-5x-failover-http-random-shutdown-total-sync","eap-5x-failover-http-random-undeploy-buddy-async","eap-5x-failover-http-random-undeploy-buddy-sync","eap-5x-failover-http-random-undeploy-total-async","eap-5x-failover-http-random-undeploy-total-sync","eap-5x-failover-http-shutdown-buddy-async","eap-5x-failover-http-shutdown-buddy-sync","eap-5x-failover-http-shutdown-total-async","eap-5x-failover-http-shutdown-total-sync","eap-5x-failover-http-undeploy-buddy-async","eap-5x-failover-http-undeploy-buddy-sync","eap-5x-failover-http-undeploy-total-async","eap-5x-failover-http-undeploy-total-sync"]
def jobs = ["eap-5x-failover-http-random-jvmkill-buddy-async","eap-5x-failover-http-random-jvmkill-buddy-sync","eap-5x-failover-http-random-jvmkill-total-async","eap-5x-failover-http-random-jvmkill-total-sync","eap-5x-failover-http-random-shutdown-buddy-async","eap-5x-failover-http-random-shutdown-buddy-sync","eap-5x-failover-http-random-shutdown-total-async","eap-5x-failover-http-random-shutdown-total-sync","eap-5x-failover-http-random-undeploy-buddy-async","eap-5x-failover-http-random-undeploy-buddy-sync","eap-5x-failover-http-random-undeploy-total-async","eap-5x-failover-http-random-undeploy-total-sync"]

for (job in jobs){
   item = Hudson.instance.getItem(job)

   println "\n* processing ${item.name}"

   // TORENAME item.renameTo("radotest2")
   
   builders = item.buildersList
      
   // SCM
   scm = item.scm
   locs = scm.@locations
   def delLoc = null
   for(loc in locs){
      if(loc.@remote.contains("script")){
         println "Removing " + loc.@remote
         delLoc = loc
      }
   }
   scm.@locations = locs.minus(delLoc)


   // SF BUILDER
   sfBuilder = builders.get(SmartFrogBuilder.DESCRIPTOR)
   sfBuilder.@sfUserHome = "etc/maven/eap-51/target/dependency"
   sfBuilder.jvmArgs  = "-server -Xmx4g -XX:+UseLargePages"
   //sfBuilder.@scriptContent = sfBuilder.scriptContent.replaceAll("org/jboss/smartfrog/test/ejb3/template-failover-ejb3-order.sf", "org/jboss/smartfrog/eap/failover/template-ejb3-order.sf")
   sfBuilder.@scriptContent = sfBuilder.scriptContent.replaceAll("org/jboss/smartfrog/test/failover/template-http-order.sf", "org/jboss/smartfrog/eap/failover/template-http-order.sf")
   sfBuilder.hosts = "perf09 perf01 perf02 perf03 perf04 perf05"

   // update maven plugin
   mavenb = builders.get(Maven.DESCRIPTOR)
   mavenb.@pom = "maven/eap-51/hudson-failover.xml"

   // make the workspace delete after the run
   list = item.getPublishersList()
   if(!list.contains(hudson.plugins.ws_cleanup.WsCleanup.DESCRIPTOR)){
      list.add(new hudson.plugins.ws_cleanup.WsCleanup())
   }


   // persist to file (helps on hudson restart ;)
   item.save()
}

