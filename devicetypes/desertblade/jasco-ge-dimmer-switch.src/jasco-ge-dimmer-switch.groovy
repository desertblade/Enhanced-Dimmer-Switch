/**
 *  GE/Jasco Dimmer Switch
 *	Author: Ben W. (@desertBlade)
 *
 * Based off of the Dimmer Switch under Templates in the IDE 
 * Copyright (C) Ben W.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

metadata {
	definition (name: "Jasco/GE Dimmer Switch", namespace: "desertblade", author: "Ben W.") {
		capability "Switch Level"
		capability "Actuator"
		capability "Indicator"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
        
        command "updateSettings"
        command "lowLevel"
        command "mediumLevel"
        command "highLevel"

		fingerprint deviceId: "0x1101", inClusters: "0x26"
        
        /* 
         GE 12724:
         Product Type ID: 0x4944
		Product ID: 0x3031
        
        Product Type ID: 0x4944
		Product ID: 0x3033
        
        Product Type ID: 0x4944
		Product ID: 0x3035
       
       Jasco In-Wall Dimmer (45712)
       Product Type ID: 0x4457
       Product ID: 0x3230
       
       
       Product Type ID: 0x4944
		Product ID: 0x3135
        
        
        https://community.smartthings.com/t/new-z-wave-fingerprint-format/48204
        
        */
        
	}

	preferences {
       input ( "stepSize", "number", title: "zWave Size of Steps in Percent",
              defaultValue: 1, range: "1..99", description:"Enter Value, Default 1", required: false)
       input ( "stepDuration", "number", title: "zWave Steps Intervals in ms",
              defaultValue: 3,range: "1..255", description:"Enter Value, Default 30", required: false)
       input ( "invertSwitch", "boolean", title: "Is the switch Inverted?",
              defaultValue: false, required: false)
       input ( "manualStepSize", "number", title: "Manual Size of Steps in Percent",
              defaultValue: 1, range: "1..99", description:"Enter Value, Default 1", required: false)
       input ( "manualStepDuration", "number", title: "Manual Steps Intervals in ms",
              defaultValue: 3,range: "1..255", description:"Enter Value, Default 30", required: false)
    input ( "lowLevel", "number", title: "Low Light Level",
              description: "Enter in a number between 1-99.", range: "1..99", defaultValue: 10, required: false)
       input ( "mediumLevel", "number", title: "Medium Light Level",
              description: "Enter in a number between 1-99.", range: "1..99", defaultValue: 50, required: false)
       input ( "highLevel", "number", title: "High Light Level",
              description: "Enter in a number between 1-99.", range: "1..99", defaultValue: 99, required: false)
            
		
    }
    

	simulator {
		status "on":  "command: 2003, payload: FF"
		status "off": "command: 2003, payload: 00"
		status "09%": "command: 2003, payload: 09"
		status "10%": "command: 2003, payload: 0A"
		status "33%": "command: 2003, payload: 21"
		status "66%": "command: 2003, payload: 42"
		status "99%": "command: 2003, payload: 63"

		// reply messages
		reply "2001FF,delay 5000,2602": "command: 2603, payload: FF"
		reply "200100,delay 5000,2602": "command: 2603, payload: 00"
		reply "200119,delay 5000,2602": "command: 2603, payload: 19"
		reply "200132,delay 5000,2602": "command: 2603, payload: 32"
		reply "20014B,delay 5000,2602": "command: 2603, payload: 4B"
		reply "200163,delay 5000,2602": "command: 2603, payload: 63"
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
		}

		standardTile("indicator", "device.indicatorStatus", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "when off", action:"indicator.indicatorWhenOn", icon:"st.indicators.lit-when-off"
			state "when on", action:"indicator.indicatorNever", icon:"st.indicators.lit-when-on"
			state "never", action:"indicator.indicatorWhenOff", icon:"st.indicators.never-lit"
		}
      

		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		valueTile("level", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "level", label:'${currentValue} %', unit:"%", backgroundColor:"#ffffff"
		}
		
       	standardTile("lowLevel", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'Low', action:"lowLevel"
            state "${lowLevel}", label:"on", action:"lowLevel"
		}
       	
        standardTile("mediumLevel", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "defulat", label:'Medium', action:"mediumLevel"
		}
        
        standardTile("highLevel", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'High',action:"highLevel"
		}
        
        standardTile("updateSettings", "device.updateSettings", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default" , action:"updateSettings", icon:"st.secondary.configure"
        }


		main(["switch"])
		details(["switch", "lowLevel", "mediumLevel", "highLevel", "level", "indicator", "refresh", "updateSettings"])

	}
}

def parse(String description) {
	def item1 = [
		canBeCurrentState: false,
		linkText: getLinkText(device),
		isStateChange: false,
		displayed: false,
		descriptionText: description,
		value:  description
	]
	def result
	def cmd = zwave.parse(description, [0x20: 1, 0x26: 1, 0x70: 1])
	if (cmd) {
		result = createEvent(cmd, item1)
	}
	else {
		item1.displayed = displayed(description, item1.isStateChange)
		result = [item1]
	}
    
          if ( state.offCount > 1 ) {
       		//def midLevel = Math.max(Math.min(midLevel, 99), 1)
            //state.offCount = 0
            log.debug "if"
       		result = [response(zwave.basicV1.basicSet(value: 20).format())]
       }
  /* 
    if ( result.value[0] == "on" && state.offCount == 1 ) {
    	state.offCount = 0
       	//def highLevel = Math.max(Math.min(highLevel, 99), 1)
    	result << response(zwave.basicV1.basicSet(value: lowLevel).format())
      } else if ( result.value[0] == "on" && state.offCount == 2 ) {
      	state.offCount = 0
        	//def lowLevel = Math.max(Math.min(lowLevel, 99), 1)
       		result << response(zwave.basicV1.basicSet(value: mediumLevel).format())
       } else if ( state.offCount > 1 ) {
       		//def midLevel = Math.max(Math.min(midLevel, 99), 1)
            //state.offCount = 0
       		result << response(zwave.basicV1.basicSet(value: lowLevel).format())
       }
       log.debug "end result ${result}"
        log.debug "end offcount: ${state.offCount}"
    
    
    */
    
	log.debug "Parse returned ${result?.descriptionText}"
	result
}



def createEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd, Map item1) {
		log.debug "BasicReport: ${cmd}"
    def result = doCreateEvent(cmd, item1)
	for (int i = 0; i < result.size(); i++) {
    	log.debug "Physical"
         log.trace "${result.value[0]}"
   	if ( result.value[0] == "off"  ) {
      def timeDiff = now() - state?.offCountTime 
   		log.debug "Time Diff: ${timeDiff}"
   	 	if ( timeDiff > 5000  ) {
        	state.offCountTime = now()
             state.offCount = 1
          //dimmerEvents(cmd)
            } else {
            	state.offCountTime = now()
          	 	state.offCount = state.offCount + 1
           }
           
          } else {
          	state.offCount = 0
            }
		result[i].type = "physical"
	}
    log.debug "offcount: ${state.offCount}"
    
	result
}

def createEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd, Map item1) {
log.debug "BasicSet"
	def result = doCreateEvent(cmd, item1)
	for (int i = 0; i < result.size(); i++) {
		result[i].type = "physical"
	}
	result
}



def createEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelSet cmd, Map item1) {
log.debug "MultiLevelSet"
	def result = doCreateEvent(cmd, item1)
	for (int i = 0; i < result.size(); i++) {
		result[i].type = "physical"
	}
	result
}

def createEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelReport cmd, Map item1) {
	log.debug "MultiLevelReport"
    def result = doCreateEvent(cmd, item1)
	result[0].descriptionText = "${item1.linkText} is ${item1.value}"
	result[0].handlerName = cmd.value ? "statusOn" : "statusOff"
	for (int i = 0; i < result.size(); i++) {
		result[i].type = "digital"
	}
	result
}

def doCreateEvent(physicalgraph.zwave.Command cmd, Map item1) {
	def result = [item1]

	item1.name = "switch"
	item1.value = cmd.value ? "on" : "off"
	item1.handlerName = item1.value
	item1.descriptionText = "${item1.linkText} was turned ${item1.value}"
	item1.canBeCurrentState = true
	item1.isStateChange = isStateChange(device, item1.name, item1.value)
	item1.displayed = item1.isStateChange

	if (cmd.value >= 5) {
		def item2 = new LinkedHashMap(item1)
		item2.name = "level"
		item2.value = cmd.value as String
		item2.unit = "%"
		item2.descriptionText = "${item1.linkText} dimmed ${item2.value} %"
		item2.canBeCurrentState = true
		item2.isStateChange = isStateChange(device, item2.name, item2.value)
		item2.displayed = false
		result << item2
	}
	result
}


private dimmerEvents(physicalgraph.zwave.Command cmd) {
	def value = (cmd.value ? "on" : "off")
	def result = [createEvent(name: "switch", value: value)]
	if (cmd.value && cmd.value <= 100) {
		result << createEvent(name: "level", value: cmd.value, unit: "%")
	}
	return result
}


def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
	log.debug "ConfigurationReport $cmd"
	def value = "when off"
	if (cmd.configurationValue[0] == 1) {value = "when on"}
	if (cmd.configurationValue[0] == 2) {value = "never"}
	createEvent([name: "indicatorStatus", value: value])
}

def zwaveEvent(physicalgraph.zwave.commands.hailv1.Hail cmd) {
	createEvent([name: "hail", value: "hail", descriptionText: "Switch button was pressed", displayed: false])
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	log.debug "manufacturerId:   ${cmd.manufacturerId}"
	log.debug "manufacturerName: ${cmd.manufacturerName}"
	log.debug "productId:        ${cmd.productId}"
	log.debug "productTypeId:    ${cmd.productTypeId}"
	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	updateDataValue("MSR", msr)
	updateDataValue("manufacturer", cmd.manufacturerName)
	createEvent([descriptionText: "$device.displayName MSR: $msr", isStateChange: false])
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelStopLevelChange cmd) {
	log.debug "StopLevelChange"
	[createEvent(name:"switch", value:"on"), response(zwave.switchMultilevelV1.switchMultilevelGet().format())]
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.debug "other command ${cmd}"
	// Handles all Z-Wave commands we aren't interested in
	[:]
}

def on() {
	delayBetween([
			zwave.basicV1.basicSet(value: 0xFF).format(),
			zwave.switchMultilevelV1.switchMultilevelGet().format()
	],5000)
}

def off() {
	delayBetween([
			zwave.basicV1.basicSet(value: 0x00).format(),
			zwave.switchMultilevelV1.switchMultilevelGet().format()
	],5000)
}


def setLevel(value) {
	log.debug "setLevel >> value: $value"
	def valueaux = value as Integer
	def level = Math.max(Math.min(valueaux, 99), 0)
	if (level > 0) {
		sendEvent(name: "switch", value: "on")
	} else {
		sendEvent(name: "switch", value: "off")
	}
//	sendEvent(name: "level", value: level, unit: "%")
	delayBetween ([zwave.powerlevelV1.powerlevelSet(powerLevel: 67).format(), zwave.basicV1.basicSet(value:  0xFF).format()], 500)
    //delayBetween ([zwave.commands.powerlevelv1.PowerlevelSet(value: level).format()], 500)
}

def newsetLevel(value) {
	log.debug "setLevel >> value: $value"
	def valueaux = value as Integer
    def duration = 6000
	def level = Math.max(Math.min(valueaux, 99), 0)
	if (level > 0) {
		sendEvent(name: "switch", value: "on")
	} else {
		sendEvent(name: "switch", value: "off")
	}
	sendEvent(name: "level", value: level, unit: "%")
	setLevel(level, duration)
}

def setLevel2(value, duration) {
	log.debug "setLevel >> value: $value, duration: $duration"
	def valueaux = value as Integer
	def level = Math.max(Math.min(valueaux, 99), 0)
	def dimmingDuration = duration < 128 ? duration : 128 + Math.round(duration / 60)
    log.debug ( "dimmingDuration: ${dimmingDuration}" )
	def getStatusDelay = duration < 128 ? (duration*1000)+2000 : (Math.round(duration / 60)*60*1000)+2000
	delayBetween ([zwave.switchMultilevelV2.switchMultilevelSet(value: level, dimmingDuration: duration).format(),
				   zwave.switchMultilevelV1.switchMultilevelGet().format()], dimmingDuration)
}

def presetLevel(level){
log.debug ("Setting preset Level to ${level}")
	if ( device.currentState("level").value.toInteger() != level.toInteger() || device.currentState("switch").value == "off" ) {
			setLevel(level) 
     } else {
        off()
     }
}

def highLevel() {
	presetLevel(highLevel)
}

def mediumLevel() {
	setLevel(mediumLevel)
}

def lowLevel() {
	presetLevel(lowLevel)
}


def poll() {
log.debug "Poll"
	zwave.switchMultilevelV1.switchMultilevelGet().format()
}

def refresh() {
	log.debug "refresh() is called"
	def commands = []
	commands << zwave.switchMultilevelV1.switchMultilevelGet().format()
	if (getDataValue("MSR") == null) {
		commands << zwave.manufacturerSpecificV1.manufacturerSpecificGet().format()
	}
	delayBetween(commands,100)
}

def indicatorWhenOn() {
	sendEvent(name: "indicatorStatus", value: "when on")
	zwave.configurationV1.configurationSet(configurationValue: [1], parameterNumber: 3, size: 1).format()
}

def indicatorWhenOff() {
	sendEvent(name: "indicatorStatus", value: "when off")
	zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 3, size: 1).format()
}

def indicatorNever() {
	sendEvent(name: "indicatorStatus", value: "never")
	zwave.configurationV1.configurationSet(configurationValue: [2], parameterNumber: 3, size: 1).format()
}

def invertSwitch(invert) {
	if (invert) {
		zwave.configurationV1.configurationSet(configurationValue: [1], parameterNumber: 4, size: 1).format()
	}
	else {
		zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 4, size: 1).format()
	}
}

def updateSettings() {
	log.debug("Updating Switch Settings")

    //lets make sure we are in the the right ranges
    def stepSize = Math.max(Math.min(stepSize.toInteger(), 99), 1)
   // def newStepDuration = Math.max(Math.min((stepDuration), 255), 1)
   def manualStepSize = Math.max(Math.min(manualStepSize, 99), 1)
   // def newManualStepDuration = Math.max(Math.min((manualStepDuration), 255), 1)
   
     def cmds = []
        cmds << zwave.configurationV1.configurationSet(configurationValue: [stepSize], parameterNumber: 7, size: 1).format()
        cmds << zwave.configurationV1.configurationSet(configurationValue: [stepDuration], parameterNumber: 8, size: 1).format()
        cmds << zwave.configurationV1.configurationSet(configurationValue: [manualStepSize], parameterNumber: 9, size: 1).format()
        cmds << zwave.configurationV1.configurationSet(configurationValue: [manualStepDuration], parameterNumber: 10, size: 1).format()
        
        if (invertSwitch.toBoolean()) {
		    cmds << zwave.configurationV1.configurationSet(configurationValue: [1], parameterNumber: 4, size: 1).format()
		} else {
			cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 4, size: 1).format()
		}
        
        //Getting the new settings (check logs) -- Don't really use for anything else
      
        cmds << zwave.configurationV1.configurationGet(parameterNumber: 7).format()
   		cmds << zwave.configurationV1.configurationGet(parameterNumber: 8).format()
    	cmds << zwave.configurationV1.configurationGet(parameterNumber: 9).format()
    	cmds << zwave.configurationV1.configurationGet(parameterNumber: 10).format()
        cmds << zwave.configurationV1.configurationGet(parameterNumber: 4).format()
    
    delayBetween(cmds, 500)
}