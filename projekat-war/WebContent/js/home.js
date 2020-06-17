Vue.component('homepage', {
	data: function() {
		return {
			runningAgents: null,
			runningAgentsCb: null,
			agentTypes: null,
			agentAlias: "",
			selectedType: undefined,
			selectedAgent: undefined
		}
	},
	template:
		`
		<div id="master-wrap">
			<div class="container">
				<div class="content-wrap loggedUsersPanel">
					<h1>Running Agents</h1>
					<div id="loggedInUsers">
						<div v-for="ra in runningAgents">
							<div class="users-logged"><label class="username">{{ra.agentType.name}}-{{ra.agentCenter.alias}}@{{ra.agentCenter.address}}</label></div>
						</div>
					</div>
				</div>
				<div class="content-wrap sendMessage">
					<div class="message-form">
						<h1>Start Agent</h1>
						<form id="composeMessageForm" class="box" method="put" v-on:submit.prevent="startAgent">
							<input class="txtb" placeholder="Agent Alias" type="text" v-model="agentAlias">
							<div>
								<label style="color:#9a9da0;">Agent Type:</label>
								<select v-model="selectedType">
									<option v-for="agent in agentTypes">{{agent.name}}</option>
								</select>
							</div>
							<input class="signup-btn" type="submit" value="Start">
						</form>
					</div>
				</div>
				<div class="content-wrap sendMessage">
					<div class="message-form">
						<h1>Stop Agent</h1>
						<form id="stopAgentForm" class="box" method="delete" v-on:submit.prevent="stopAgent">
							<div>
								<label style="color:#9a9da0;">Agent:</label>
								<select v-model="selectedAgent">
									<option v-for="agentr in runningAgentsCb">{{agentr.agentType.name}}-{{agentr.agentCenter.alias}}@{{agentr.agentCenter.address}}</option>
								</select>
							</div>
							<input class="signup-btn" type="submit" value="Stop">
						</form>
					</div>
				</div>
			</div>
		</div>
		`,
	mounted: function() {
		var a = this;
		
		axios.get('rest/managers/agents/running')
			.then(function(response) {
				a.runningAgents = response.data;
				a.runningAgentsCb = response.data;
			})
			.catch(function(error) {
				alert(error.response.data);
			});
		
		axios.get('rest/managers/agents/classes')
			.then(function(response) {
				a.agentTypes = response.data;
			})
			.catch(function(error) {
				alert(error.response.data);
			});
		
		var socket;
		var host = "ws://" + window.location.host + "/projekat-war/ws"
		
		try {
			socket = new WebSocket(host);
			
			socket.onmessage = function(msg) {
				if (msg.data === "AGENT_STARTED" || msg.data === "AGENT_STOPPED") {
					axios.get('rest/managers/agents/running')
						.then(function(response) {
							a.runningAgents = response.data;
							a.runningAgentsCb = response.data;
						})
						.catch(function(error) {
							alert(error.response.data);
						});
				}
			}
			
			socket.onclose = function() {
				socket = null;
			}
			
		} catch (exception) {
			console.log('Error' + exception);
		}
	},
	methods: {
		startAgent: function() {
			var a = this;
			
			if (a.agentAlias !== "" && a.selectedType !== undefined) {
				let type;
				let temp = a.agentTypes;
				for (let i = 0; i < temp.length; i++) {
					if (temp[i].name === a.selectedType) {
						type = temp[i];
						break;
					}
				}
				
				axios.put('rest/managers/agents/running/' + a.selectedType + '/' + a.agentAlias, type)
					.then(function(response) {
						a.selectedType = undefined;
						a.agentAlias = "";
					})
					.catch(function(error) {
						alert(error.response.data);
					});
				
				document.getElementById("composeMessageForm").reset();
			}
		},
		stopAgent: function() {
			var a = this;
			
			if (a.selectedAgent !== undefined) {
				let agent;
				let temp = a.runningAgentsCb;
				let splitted = a.selectedAgent.split('@')[0].split('-');
				for (let i = 0; i < temp.length; i++) {
					if (splitted[0] === temp[i].agentType.name && splitted[1] === temp[i].agentCenter.alias) {
						agent = temp[i];
						break;
					}
				}
				
				axios.post('rest/managers/agents/running/' + splitted[0] + splitted[1] , agent)
			}
		}
	}
});
