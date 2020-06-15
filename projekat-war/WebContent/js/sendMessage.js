Vue.component('sendMessage', {
	data: function() {
		return {
			agentTypes: null,
			performatives: null
		}
	},
	template:
		`
		<div id="master-wrap">
			<div class="container">
				<select>
					<option v-for="agent in agentTypes">{{agent.name}}</option>
				</select>
				<select>
					<option v-for="p in performatives">{{p}}</option>
				</select>
			</div>
		</div>
		`,
	mounted: function() {
		var a = this;
		
		axios.get('rest/managers/agents/classes')
			.then(function(response) {
				a.agentTypes = response.data;
			})
			.catch(function(error) {
				alert(error.response.data);
			});

		axios.get('rest/managers/messages')
			.then(function(response) {
				a.performatives = response.data;
			})
			.catch(function(error) {
				alert(error.response.data);
			});
		
	}
});